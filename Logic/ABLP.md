
- [The ABLP logic](#the-ablp-logic)
  - [The says modality](#the-says-modality)
- [ABLP logic in N3](#ablp-logic-in-n3)
  - [Quoting](#quoting)
  - [Roles](#roles)

# The ABLP logic

In the early 1990s Mart́in Abadi, Michael Burrows, and Butler Lampson of Digital Equipment Corporation and Gordon Plotkin of the University of Edinborough wrote some key papers on authentication and access control in distributed systems, developing a modal logic built around a basic "says" operator. (Michael Burrows wrote the AltaVista search engine) 

The key papers are:
 * 1992: [Authentication in Distributed Systems: Theory and Practice](https://dl.acm.org/doi/pdf/10.1145/138873.138874)
 * 1993: [A Calculus for Access Control in Distributed Systems](https://dl.acm.org/doi/pdf/10.1145/155183.155225)

 The work continued in the 2000s with:
* 2006: [Access control in a core calculus of dependency](https://dl.acm.org/doi/abs/10.1145/1159803.1159839) by Martin Abadi
* 2008: [A Modal Deconstruction of Access Control Logics](https://link.springer.com/chapter/10.1007/978-3-540-78499-9_16), by Martin Abadi and Deepak Garg
* 2009: [Proof Theory for Authorization Logic and Its Application to a Practical File System](https://people.mpi-sws.org/~dg/papers/papers.html#theses) Deepak Garg's Ph.D. dissertation,  2009.

We want to see how we can use the formalism developed there to explain an aspect of access control in Solid, for which we have developed [Use Cases](../UseCases/UseCases.md).

ABLP logic is built on two concepts:
 * the $\text{says}$ modality indexed by principal 
 * a lattice of principals and operators on them 

## The says modality

The 1993 paper "A Calculus for Access Control in Distributed Systems" states that the says operator is a modal one.

$$
  s \text{ says } p
$$


The paper 




 # ABLP logic in N3

 The ABLP logic is built around a `says` operator which relates a subject to a statement. 

$$
  s \text{ says } p
$$

 In RDF we can simply name that relation with a URL `ablp:`says` and specify that it relates an agent to a statement. This was done by Dan Connolly around 2009 in the [speech n3 ontology](https://www.w3.org/2001/tag/dj9/speech) which is part of [A Model of Authority on the Web](https://www.w3.org/2001/tag/dj9/story.html). The ontology is published on the w3c servers as [/2001/tag/dj9/speech](https://www.w3.org/2001/tag/dj9/speech).


This is also being worked on by the [N3 community group](https://www.w3.org/community/n3-dev/), on [github.com/w3c/N3](https://github.com/w3c/N3/):
* [speech.n3](https://github.com/w3c/N3/blob/master/tests/N3Tests/07test/speech.n3) 
* [httpspeech.n3](https://github.com/w3c/N3/blob/master/tests/N3Tests/07test/httpspeech.n3)
* [badmeta.n3](https://github.com/w3c/N3/blob/master/tests/N3Tests/07test/badmeta.n3)

The community group has produced [Notation3 Language - Draft Community Group Report](https://w3c.github.io/N3/reports/20230703/)

The $\text{says}$ relation is defined as follows:

```turtle
@prefix ablp: <https://www.w3.org/2001/tag/dj9/speech#> .

ablp:says :says s:label "says";
     s:domain :Principal.
```

The range is not defined, though it really should be either a graph or a dereferenceable information resource, which an HTTP GET would return an RDF Graph. That use is visible from the [httpspeech.n3](https://github.com/w3c/N3/blob/master/tests/N3Tests/07test/httpspeech.n3) example.

## Quoting

ABLP logic's central operator on principals is the $|$ operator which takes two principals and returns a new principal. It is defined as follows:

$$
A|B \text{ says } s \equiv A \text{ says } B \text{ says } s
$$

In other words: when the principal $A|B$ says something it is equivalent to $A$ saying $B$ says something, or $A$ quoting $B$ as saying. Hence $A|B$ is read "$A$ quoting $B$".

Dan Connolly defined it as

```n3
@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
@prefix : <https://www.w3.org/2001/tag/dj9/speech#> .

:quoting a owl:FunctionalProperty;
         rdfs:domain rdf:List; # of 2 principals
         rdfs:range :Principal .
```

Arguably there is no need to limit the list to 2 since the operator is associative, that is $A|(B|C) = (A|B)|C$.

Dan Connolly in [Decision-making in ABLP logic](https://www.w3.org/2001/tag/dj9/refi_ex.html) gives the example of Sam hearing Melissa-on-the-Phone saying that Mellisa quoting the Wall Street Journal `WSJ` says that the fed rate is 4.5.  He then writes it
like this in n3 where the "is ... of" construct gives the relation in the opposite direction:

```n3
@prefix c: <https://www.w3.org/2001/tag/dj9/speech#>
MelissaByPhone c:says {
  [ is c:quoting of (Melissa WSJ ) ] c:says { Fed rate 4.5 }
}.
```

In the latest N3 this would be written

```n3
@prefix c: <https://www.w3.org/2001/tag/dj9/speech#>
:MelissaByPhone c:says {
  [ <- c:quoting (:Melissa :WSJ ) ] c:says { :Fed :rate 4.5 }
}.
```

## Roles

Roles are introduced in the 1993 paper as follows:

> There are many situations in which a principal may wish to reduce his powers. We now describe a few, as motivation for our treatment of roles. They are all examples of the principle of “least privilege,” according to which a principal should have only the privileges it needs to accomplish its task.

A user $U$ in a role $R$, written $U \text{ as } R$ is modeled as $A | R$ (expressed as "A quoting R") where both A is a Principal, and R is a Role (which is a type of Principal). That is both $\text{ as }$ and $|$ are binary operators on principals, that return a new principal.
(todo: we need a definition of Principals, Roles)

$$
A \text{ as } R = A | R 
$$

So we could create an `as`` relation

```n3
@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .
@prefix : <https://www.w3.org/2001/tag/dj9/speech#> .

:as owl:subpropertyOf :quoting;
      rdfs:domain rdf:List; # of 1 principal and a role
      rdfs:range :Role .
```

Note that this does not give us the precision we want. A more type-safe model would be that the domain is a tuple type where the first element of the tuple is a principal and every other element of the tuple is a role. 
Or equivalently a pair of a principal and a list of roles. 
Or we could also specify a type `Role` with two required relations:

```n3
@prefix owl: <http://www.w3.org/2002/07/owl#> .

<#Role> a owl:Class;
   rdfs:subClassOf <#Principal>.

:principal a owl:ObjectProperty;
   rdfs:range <#Principal>.
:as owl:ObjectProperty;
   rdfs:range <#Role>;

<#Role> owl:hasKey (:principal :as).
```

Then we can write a client b

```turtle
<#r1> a :Authorization;
  :mode :Read, :Write;
  :agent [ :principal <#Alice>;
           :as <https://photo.app/demo#> ]; 
  :accessToClass [ :subdirs </app/photo/> ] .
```



