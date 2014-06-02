# owl2jsonld

Convert RDFS/OWL ontology to JSON-LD context.

This tool intends to generate a [JSON-LD](http://www.w3.org/TR/json-ld/) `@context`
for concepts (classes and properties) found in the specified 
[OWL](http://www.w3.org/TR/owl2-primer/) ontology.


# Status: EARLY DEVELOPMENT

This project is still at an early development status and do not yet
have the capabilities described here. :-)


## Installation

Install dependencies:
 * [git](http://www.git-scm.com/)
 * [leiningen](http://leiningen.org/)
 * [owlapi-clj](https://github.com/stain/owlapi-clj) (see below)

    $ git clone https://github.com/stain/owlapi-clj.git
    $ cd owlapi-clj 
    $ lein install 
    $ cd ..

TODO: Ensure owlapi-clj is in Clojars to avoid manuall install.

Then check out [owl2jsonld](https://github.com/stain/owl2jsonld) (this project)
and build it:

    $ git clone https://github.com/stain/owl2jsonld.git
    $ cd owl2jsonld
    $ lein uberjar


## Usage

Generate JSON-LD context from the given RDFS/OWL ontology URL(s):

    $ java -jar owl2jsonld-*-standalone.jar [-a|-n] [-c|-p] [-P PREFIX] [-i] [-o OUTPUT] [-e] <ONTOLOGY ...>

### Options

| Opt| Option                      | Description
-----|-----------------------------|--------------------------
| -a | --all-imports               | Include all OWL-imported concepts (default: only directly referenced elements)
| -n | --no-imports                | Exclude all OWL-imported concepts
| -c | --classes                   | Include only classes
| -p | --properties                | Include only properties
| -P `PREFIX`  | --prefix=`PREFIX` | JSON-LD prefix to use for generated concepts (default: no prefix)
| -i | --inherit                   | Inherit prefixes from the source ontology
| -o `OUTPUT`  | --output=`OUTPUT` | Output file for generated JSON-LD context (default: write to STDOUT)
| -e | --embed                     | Embed ontology definition serialized as JSON-LD

## Examples

    $ java -jar owl2jsonld-0.1.0-standalone.jar http://purl.org/pav/

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

## License

Copyright © 2014 [Stian Soiland-Reyes](http://orcid.org/0000-0001-9842-9718).

This source code is distributed under the 
[Eclipse Public License](http://www.eclipse.org/legal/epl-v10.html) 
either version 1.0 or (at your option) any later version.

The uberjar contains [OWL API](http://owlapi.sourceforge.net/), which is
distributed under the alternative of [LGPL](http://www.gnu.org/licenses/lgpl)
or [Apache license 2.0](http://www.apache.org/licenses).
