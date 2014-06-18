# owl2jsonld

[![Build Status](https://travis-ci.org/stain/owl2jsonld.svg)](https://travis-ci.org/stain/owl2jsonld)
[![doi:10.5281/zenodo.10563](https://zenodo.org/badge/doi/10.5281/zenodo.10563.png)](http://dx.doi.org/10.5281/zenodo.10563)

*Convert OWL ontology to JSON-LD context*

This tool generate a [JSON-LD](http://www.w3.org/TR/json-ld/) `@context`
for concepts (classes and properties) found in the specified 
[OWL](http://www.w3.org/TR/owl2-primer/) or RDFS ontology.


## Download

The [official releases](https://github.com/stain/owl2jsonld/releases)
contain an *uberjar* (standalone) JAR, which bundles all dependencies
and can be used as a commandline tool.

The uberjar contains [OWL API](http://owlapi.sourceforge.net/), which is
distributed under the alternative of [LGPL](http://www.gnu.org/licenses/lgpl)
or [Apache license 2.0](http://www.apache.org/licenses).


## Installing from source

Install dependencies:
 * [OpenJDK 7](http://openjdk.java.net/) or [Oracle Java 7 SE](http://www.oracle.com/technetwork/java/javase/downloads/index.html?ssSourceSiteId=ocomen)
 * [git](http://www.git-scm.com/)
 * [leiningen](http://leiningen.org/)


Check out [owl2jsonld](https://github.com/stain/owl2jsonld) (this project)
and build it:

    $ git clone https://github.com/stain/owl2jsonld.git
    $ cd owl2jsonld
    $ lein uberjar


## Command line usage

Generate JSON-LD context from the given RDFS/OWL ontology URL(s):

    $ java -jar owl2jsonld-*-standalone.jar [-a|-n] [-c|-p] [-P PREFIX] [-i] [-o OUTPUT] [-e] ONTOLOGY ...

To avoid making a uberjar (e.g. during development), you may alternatively run
the tool using:

    $ lein run -- [options] ONTOLOGY`


### Options

```
Convert OWL ontology to JSON-LD context

Usage: owl2jsonld [options] ONTOLOGY...

Options:
  -a, --all-imports    Include all OWL-imported concepts (default: only directly referenced elements)
  -d, --only-defined   Include only concepts which are rdfs:isDefinedBy the specified ontologies
  -c, --classes        Include only classes
  -p, --properties     Include only properties
  -P, --prefix PREFIX  JSON-LD prefix to use for generated concepts (default: no prefix)
  -i, --inherit        Inherit prefixes from the source ontology
  -o, --output OUTPUT  Output file for generated JSON-LD context (default: write to STDOUT)
  -v, --verbose        Verbose output on STDERR
  -h, --help


More info: README.md or https://github.com/stain/owl2jsonld
```


### Example

    $ java -jar owl2jsonld-0.2.0-standalone.jar http://purl.org/pav/

```json    
{ "@context": {
    "authoredBy": { "@id": "http://purl.org/pav/authoredBy",
                    "@type": "@id"
                  },
    "authoredOn": { "@id": "http://purl.org/pav/authoredOn",
                    "@type" "http://www.w3.org/2001/XMLSchema#dateTime"
                  }
    }
    ...
}
```

## Usage from Clojure 

To use from [Clojure](http://clojure.org/) with 
[Leiningen](http://leiningen.org/), 
use the [Clojar dependency owl2jsonld](https://clojars.org/owl2jsonld):

    [owl2jsonld "0.2.0"]


## License

Copyright © 2014 [Stian Soiland-Reyes](http://orcid.org/0000-0001-9842-9718), [University of Manchester](http://www.cs.manchester.ac.uk/).

This source code is distributed under the 
[Eclipse Public License](http://www.eclipse.org/legal/epl-v10.html) 
either version 1.0 or (at your option) any later version.

The uberjar contains [OWL API](http://owlapi.sourceforge.net/), which is
distributed under the alternative of [LGPL](http://www.gnu.org/licenses/lgpl)
or [Apache license 2.0](http://www.apache.org/licenses).

# Cite

Cite as:

Soiland-Reyes, Stian (2014). owl2jsonld 0.1.0. ZENODO. [10.5281/zenodo.10563](http://dx.doi.org/10.5281/zenodo.10563)

(Check https://github.com/stain/owl2jsonld#cite for latest version/citation)
