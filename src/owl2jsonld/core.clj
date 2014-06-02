(ns owl2jsonld.core
  (:require 
    [clojure.tools.cli :refer [parse-opts]]
    [clojure.java.io :refer [as-file output-stream writer]]
    [clojure.string :as string]
    [cheshire.core :refer [generate-stream]]
    )
  (:gen-class))



(def cli-options
  [ ; When changing, remember to also update README.md
    ["-a" "--all-imports"    "Include all OWL-imported concepts (default: only directly referenced elements)"]
    ["-n" "--no-imports"     "Exclude all OWL-imported concepts"]
    ["-c" "--classes"        "Include only classes"]
    ["-p" "--properties"     "Include only properties"]
    ["-P" "--prefix PREFIX"  "JSON-LD prefix to use for generated concepts (default: no prefix)"]
    ["-i" "--inherit"        "Inherit prefixes from the source ontology"]
    ["-o" "--output OUTPUT"  "Output file for generated JSON-LD context (default: write to STDOUT)"
      :parse-fn as-file 
    ]
    ["-e" "--embed"          "Embed ontology definition serialized as JSON-LD"]
    ["-v" "--verbose"        "Verbose output on STDERR"] 
    ["-h" "--help"]
])

(defn usage [options-summary]
  (->> ["Convert OWL ontology to JSON-LD context"
        ""
        "Usage: owl2jsonld [options] ONTOLOGY..."
        ""
        "Options:"
        options-summary
        ""
        ""
        "More info: README.md or https://github.com/stain/owl2jsonld"]
       (string/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn exit [status msg]
  (binding [*out* (if (> status 0) *err* *out*)] 
    (println msg))
  (System/exit status))


(def ^:dynamic *log* nil)

(defn log [& msg]
  (if *log*
    (binding [*out* *log*] (apply println msg))))

(defn owl2jsonld
  [urls {:keys 
          [all-imports no-imports classes properties prefix inherit output embed]
         :or { output System/out }}] 
  (with-open [out (writer output)]
      (generate-stream {"@context" {} } out )))

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    ;; Handle help and error conditions
    (cond
      (:help options) (exit 0 (usage summary))
      (not= (count arguments) 1) (exit 1 (usage summary))
      errors (exit 2 (error-msg errors))
      (and (:all-imports options) (:no-imports options)) (exit 3 (error-msg 
            ["Can't combine --all-imports and --no-imports"]))
      
      )
    (binding [*log* (if (:verbose options) *err* nil)]
      (owl2jsonld arguments options))))
