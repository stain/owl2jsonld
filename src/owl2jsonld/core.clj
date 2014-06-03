(ns owl2jsonld.core
  (:require 
    [owlapi-clj.core :as owlapi]
    )
  (:gen-class))

(def ^:dynamic *log* nil)

(defn log [& msg]
  (if *log*
    (binding [*out* *log*] (apply println msg))))

(defn owl2jsonld 
  [urls {:keys [all-imports no-imports classes properties prefix inherit embed]}]

  (let [ontologies (map owlapi/load-ontology urls)]
    ; Ensure all ontologies are loaded before we make any assumptions about
    ; classes etc.. e.g. allowing second ontology to modulate terms from the
    ; first ontology
    (dorun ontologies)

    (doseq [ontology ontologies]
      (log "Ontology" ontology)
      (log "Classes" (owlapi/classes ontology))
      (log "Properties" (owlapi/object-properties ontology))
      )
    { "@context" {} }))



