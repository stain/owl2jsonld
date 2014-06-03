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
    (log "Ontology" (first ontologies))
    (log "Classes" (.getClassesInSignature (first (first ontologies))))
    (log "Properties" (.getObjectPropertiesInSignature (first (first ontologies))))
    { "@context" {} }))



