(ns owl2jsonld.core
  (:import 
     (org.semanticweb.owlapi.model IRI 
                                   OWLNamedObject
                                   OWLClass OWLProperty
                                   OWLDataRange
                                   OWLDatatype
                                   OWLObjectProperty 
                                   OWLClassExpression
                                   ))
  (:require 
    [owlapi-clj.core :as owlapi]
    )
  (:gen-class))

(def ^:dynamic *log* nil)

(defn log [& msg]
  (if *log*
    (binding [*out* *log*] (apply println msg))))

(def ^:dynamic *options* {})      

(defn inject-prefixes [options ontology] 
  (if (not (:inherit options)) options
    (merge options
           { :prefixes (owlapi/prefixes ontology) })))

(defn prefix-for [^IRI iri] 
 (let [prefix (or
                (get-in *options*  [:prefixes (.getNamespace iri)])
                (:prefix *options*))]
   (if prefix (str prefix ":") "")))
   
;(if (:prefix *options*) 
;  (if (:prefix *options*) (str (:prefix *options*) ":") "")
;  )

(defn name-for-iri [^IRI iri]
  (str (prefix-for iri) (.getFragment iri)))

(defn jsonld-name [^OWLNamedObject named]
  (name-for-iri (.getIRI named)))

(defn named-to-jsonld [^OWLNamedObject named]
  { "@id" (str (.getIRI named)) })

(defn class-to-jsonld [^OWLClass class]
  { (jsonld-name class) (named-to-jsonld class) } )

(defn jsonld-type-for-property [^OWLProperty property]
  (if (instance? OWLObjectProperty property) 
    {"@type" "@id" }
    (let [ranges (owlapi/ranges-of-property property)]
      (cond 
        (empty? ranges) {}
        (some #(instance? OWLClassExpression %) ranges) {"@type" "@id" }
        ; If there is only one range, which is a Datatype, we can 
        ; include it in JSON-LD's @type. 
        ;; TODO: Handle restrictions etc. on 
        ; ultimately uniform datatype
        (and (empty? (rest ranges)) (instance? OWLDatatype (first ranges))) 
              {"@type" (str (.getIRI (first ranges))) }
        :else {}))))

(defn property-to-jsonld [^OWLProperty property]
  { (jsonld-name property)
    (merge (named-to-jsonld property)         
           (jsonld-type-for-property property)) } )

(defn ontology-to-jsonld [options ontology]
   (binding [*options* (inject-prefixes options ontology)]
     (log "Ontology" ontology)
        (merge
            {}
            (if (:classes options) (apply merge (map class-to-jsonld (owlapi/classes ontology))))
            (if (:properties options)
              (apply merge (concat
               (map property-to-jsonld (owlapi/object-properties ontology))
                     ;; TODO: What about annotation properties?
               (map property-to-jsonld (owlapi/data-properties ontology)))
             ))
            )))

(defn owl2jsonld 
  [urls {:keys [all-imports no-imports inherit embed]
         :as options}]
	  (let [ontologies (doall (map owlapi/load-ontology urls))
          all-ontologies (if all-imports (owlapi/loaded-ontologies) ontologies)]
     { "@context" 
           (merge (apply merge (map (partial ontology-to-jsonld options) all-ontologies)))
      }))

