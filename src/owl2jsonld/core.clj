(ns owl2jsonld.core
  (:require 
    [owlapi-clj.core :refer [load-ontology classes]]
    )
  (:gen-class))

(def ^:dynamic *log* nil)

(defn log [& msg]
  (if *log*
    (binding [*out* *log*] (apply println msg))))

(defn owl2jsonld 
  [urls {:keys [all-imports no-imports classes properties prefix inherit embed]}]

  (let [ontologies (map load-ontology urls)]
    ; Ensure all ontologies are loaded before we make any assumptions about
    ; classes etc.. e.g. allowing second ontology to modulate terms from the
    ; first ontology
    (dorun ontologies)
    (log "Ontology" ontologies)
    (log "Classes" (.getClassesInSignature (first ontologies)))
    (log "Properties" (.getObjectPropertiesInSignature (first ontologies)))
    { "@context" {} }))



