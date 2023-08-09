In the early 1990s Mart́in Abadi, Michael Burrows, and Butler Lampson of Digitial Equipment Corporation and Gordon Plotkin of the University of Edinborough wrote some key papers on authentication and access control in distributed system, developing a modal logic built around a basic "says" operator. (Michael Burrows wrote the AltaVista search engine) 

The key papers are:
 * 1992: [Authentication in Distributed Systems: Theory and Practice](https://dl.acm.org/doi/pdf/10.1145/138873.138874)) 
 * 1993: [A Calculus for Access Control in Distributed Systems](https://dl.acm.org/doi/pdf/10.1145/155183.155225)

 The work continued in the 2000s with:
* 2006: [Access control in a core calculus of dependency](https://dl.acm.org/doi/abs/10.1145/1159803.1159839) by Martin Abadi 
* ...

We want to see how we can use the formalism developed there to explain aspect of access control in Solid, for which we have developed [Use Cases](../UseCases/UseCases.md).

- [RDF mapping](#rdf-mapping)



 # ABLP logic in N3

 The ABLP logic is built around a `says` operator which relates a subject to a statement. 

 ```math
  s says p
 ```

 In RDF we can simply name that relation with a url `ablp:says` and specify that it relates an agent to a statement. This was done by Dan Connolly around 2009 in the [speech n3 ontology](https://www.w3.org/2001/tag/dj9/speech) which is part of [A Model of Authority on the Web](https://www.w3.org/2001/tag/dj9/story.html).

 ```bash
 curl -i -H "Accept: text/n3" https://www.w3.org/2001/tag/dj9/speech
 ```

This is also being worked on by the [N3 community group](https://www.w3.org/community/n3-dev/), on [github.com/w3c/N3](https://github.com/w3c/N3/):
* [speech.n3](https://github.com/w3c/N3/blob/master/tests/N3Tests/07test/speech.n3) 
* [httpspeech.n3](https://github.com/w3c/N3/blob/master/tests/N3Tests/07test/httpspeech.n3)
* [badmeta.n3](https://github.com/w3c/N3/blob/master/tests/N3Tests/07test/badmeta.n3)

The community group has produced [Notation3 Language - Draft Community Group Report](https://w3c.github.io/N3/reports/20230703/)

The says relation is defined as follows:

```turtle
@prefix ablp: <https://www.w3.org/2001/tag/dj9/speech#> .

ablp:says :says s:label "says";
     s:domain :Principal.
```

The range is not defined, though it really should be either a graph or a dereferenceable information resource, which on HTTP GET would return a Graph.


