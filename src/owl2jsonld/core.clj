(ns owl2jsonld.core
  (:import
     (org.semanticweb.owlapi.model IRI
                                   OWLClass
                                   OWLClassExpression
                                   OWLDatatype
                                   OWLDataProperty
                                   OWLDataRange
                                   OWLEntity
                                   OWLNamedObject
                                   OWLObject
                                   OWLObjectProperty
                                   OWLOntology
                                   OWLProperty
                                   OWLNamedIndividual
                                   OWLAnnotationProperty
                                   OWLAnnotation
                                   ))
  (:require
    [owlapi.core :as owlapi]
    [clojure.set :refer [intersection union]]
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

(defn annotation-property [^IRI iri]
  (.getOWLAnnotationProperty (owlapi/data-factory) iri))

(defn select [labels]
  ; TODO: add lang support
  ; TODO: implement a proper OWLAnnotationValueVisitor in clj-owlapi
  (some-> (first labels) (.getValue) (.getLiteral)))

(defn label-for-object [^OWLNamedObject object label]
  (if-not (nil? label)
    (let [prop (annotation-property (owlapi/create-iri label))]
    (select (owlapi/annotations object prop)))))

(defn jsonld-name [^OWLNamedObject named label]
  (or
    (label-for-object named label)
    (name-for-iri (.getIRI named))))

(defn named-to-jsonld [^OWLNamedObject named]
  { "@id" (str (.getIRI named)) })

(defn class-to-jsonld [label ^OWLClass class]
  { (jsonld-name class label) (named-to-jsonld class) } )

(defn individual-to-jsonld [label ^OWLNamedIndividual individual]
  { (jsonld-name individual label) (named-to-jsonld individual) } )

(defn jsonld-type-for-property [^OWLProperty property]
  (cond
    (instance? OWLObjectProperty property)
        {"@type" "@id" }
    (instance? OWLDataProperty property)
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
	        :else {}))
    :else {}))

(defn property-to-jsonld [label ^OWLProperty property]
  { (jsonld-name property label)
    (merge
           (jsonld-type-for-property property)
           (named-to-jsonld property) ) } )

(defn is-defined? [options ^OWLEntity entity]
  ; Must be RDFS:isDefinedby one of the specified ontologies specified
  (not (empty? (intersection
     (:ontology-iris options)
     (set (map #(.getValue %)
               (owlapi/annotations entity (:RDFSIsDefinedBy (owlapi/owl-types)))))))))

(defn only-valid [options entities]
  (if (:only-defined options)
    (filter (partial is-defined? options) entities)
    entities))

(defn ontology-to-jsonld [options ontology]
   (binding [*options* (inject-prefixes options ontology)]
     (log "Ontology" ontology)
     (let [label (:label options)]
       (merge
         {}
         (if (:classes options) (apply merge (map (partial class-to-jsonld label)
                                                  (only-valid options (owlapi/classes ontology)))))
         (if (:individuals options) (apply merge (map (partial individual-to-jsonld label) (owlapi/individuals ontology))))
         (if (:properties options)
          (if (:properties options)
            ;; Old
           (apply merge (concat
            (map (partial property-to-jsonld label) (only-valid options (owlapi/annotation-properties ontology)))
            (map (partial property-to-jsonld label) (only-valid options (owlapi/object-properties ontology)))
            (map (partial property-to-jsonld label) (only-valid options (owlapi/data-properties ontology))))
          )))))))


          ;; new
;;    FIXME: Why does this cause
;; java.lang.ClassCastException: clojure.lang.PersistentArrayMap cannot be cast to java.util.Map$Entry
;;    ?
;              (merge (map property-to-jsonld (only-valid options
;              (concat
;               (owlapi/annotation-properties ontology)
;               (owlapi/object-properties ontology)
;               (owlapi/data-properties ontology)
;                ))))))))


(defn ontology-iri [^OWLOntology ontology]
  (let [ontology-id (bean (.getOntologyID ontology))]
       ; Filter out nil
       (keep identity
             (vals (select-keys ontology-id [:versionIRI :ontologyIRI])))))

(defn ontology-iris [ontologies]
  (set (mapcat ontology-iri ontologies)))

(defn owl2jsonld
  [urls {:keys [all-imports no-imports inherit embed]
         :as options}]
          (owlapi/with-owl
	  (let [ontologies (doall (map owlapi/load-ontology urls))
          all-ontologies (if all-imports (owlapi/loaded-ontologies) ontologies)
          options (merge options
                         { :ontology-iris
                              (union
                                (set (map owlapi/create-iri urls))
                                (ontology-iris ontologies))})]
     { "@context"
           (merge (apply merge (map (partial ontology-to-jsonld options) all-ontologies)))
      })))
