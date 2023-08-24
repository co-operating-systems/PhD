![Dilbert Trust](https://www.bblfish.net/tmp/2023/08/DilbertTrust.png)


The logic of "says" is a logic developed at the turn of the 1990s for decentralized access control.

It models the most basic of human interaction: the act of saying something which comic strip artists model as a speech bubble relating a cartoon character to a statement he is making. The statement need not be taken to be true or false by the other characters present. It is just a factual statement that the character who made the statement uttered those words. There is of course the presumption that the person making the statement believes it to be true and is beholden to the consequences of what he said.

The "says" modal logic can be considered as an indexed modality, relating a Principal (an agent under a description) to a statement.

The logic models what we do when someone tells us something: we don't immediately believe it, but we track who said it. If we trust the person, then we can add the statement, or parts of it to our knowledge base as facts, that is take them out of their bubble and add them to our belief store. The "says" relation allows us to keep a distance from what is said before we decide to believe it or not. All access-control decisions must be based on that distancing of a guard from the content of what is said, since someone trying to access a resource is going to claim that they are allowed to do so. The Guard protecting the resource cannot start off believing whatever he is told: he has to verify the statement first.

- [The ABLP logic](#the-ablp-logic)
  - [The says modality](#the-says-modality)
- [ABLP logic in N3](#ablp-logic-in-n3)
  - [Quoting](#quoting)
  - [Conjunction](#conjunction)
  - [Roles](#roles)
  - [speaksFor](#speaksfor)
  - [controls](#controls)
  - [hand-off axiom](#hand-off-axiom)
- [Deepak Garg's 2009 BL logic](#deepak-gargs-2009-bl-logic)
  - [The claim principle](#the-claim-principle)
  - [Mapping to RDF](#mapping-to-rdf)

### todo

- more consistent math notation: use $\supset$ for implication as in the original papers.

# The ABLP logic

In the early 1990s Mart́in Abadi, Michael Burrows, and Butler Lampson of Digital Equipment Corporation and Gordon Plotkin of the University of Edinborough wrote some key papers on authentication and access control in distributed systems, developing a modal logic built around a basic "says" operator. (Michael Burrows wrote the AltaVista search engine) 

The key papers are:
 * 1992: [Authentication in Distributed Systems: Theory and Practice](https://dl.acm.org/doi/pdf/10.1145/138873.138874) Butler Lampson, Martin Abadi, Michael Burrows, and Edward Wobber
 * 1993: [A Calculus for Access Control in Distributed Systems](https://dl.acm.org/doi/pdf/10.1145/155183.155225) Martin Abadi, Michael Burrows, Butler Lampson and Gordon Plotkin

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

## Conjunction

The N3 does not define conjunction of principals A and B, written $A \land B$.
So let us do it here


```n3
:conjunction a owl:FunctionalProperty;
   s:domain rdf:List; # of 2 ore more principals
	 s:range :Principal.
```

The rule is that 

$$
\vdash A \land B \text{ says } s \equiv A \text{ says } s \land B \text{ says } s
$$

which in N3 we render as

```n3
{ [ is :conjunction of (?A ?B) ] c:says ?s } => { 
    ?A c:says ?s  . 
    ?B c:says ?s  
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

## speaksFor

The speaks for relation is defined in usual logical notation as

$$
\forall a, b: \text{Principal} .\space a \text{ speaksFor } b \implies \forall s: \text{Prop}. (a \text{ says } s \implies b \text{ says } s )
$$


In N3 we can express it as follows:

```turtle
:speaks_for s:label "speaks for";
	  s:domain :Principal;
	  s:range :Principal.

{ ?A :speaks_for ?B . ?A :says ?s } => { ?B :says ?s }.
```

Another way of thinking of it is that it states that
there is an arrow in a trust lattice of principals between
A and B with B trusting A fully.

todo: draw  diagram

## controls

An agent controls a statement if when it says that statement,
then that statement becomes or is true. This is the basic notion of a speech act.

```N3
:controls s:label "controls";
         s:domain :Principal.

{ ?A :controls ?s . ?A :says ?s } => { ?s a log:Truth } .
```


## hand-off axiom

The 1992 paper introduces the HandOff axiom as follows:

$$
\vdash (A \text{ says } (B \Rightarrow A)) \supset (B \Rightarrow A)
$$

which says that if a principal says that someone else speaks for it, then they do. Martin Abadi's 2006 paper "Access Control in a Core Calculus of Dependency" shows how the indexed monadic structure implies this axiom. 

todo: see the paper and explain 

# Deepak Garg's 2009 BL logic


 Deepak Garg, in his 2009 Ph.D. dissertation [Proof Theory for Authorization Logic and Its Application to a Practical File System](https://people.mpi-sws.org/~dg/papers/papers.html#theses) proposes some improvements on the previous work on the says modal logic and formalises it using sequent calculus. 

 The interesting question is how can we precisely translate this to RDF and N3.

## The claim principle

On page 35: "A distinguishing characteristic of BLS is that hypothetical reasoning is always performed relative to the claims of a principal k".  This is written as $\vdash^k$, and 
it allows us to reason from the point of view of an agent. It is the equivalent of the world relativity of David Lewis' Counterfactual modal logics.

What I find the most interesting is that hypothetical reasoning is always performed relative to the claims of a principal k, which is indicated in the hypothetical judgment by writing the latter as $\Sigma; \Gamma \vdash^{k} s$ .
Having defined the judgement shortcut claims as

$$
k \text{ claims } s \equiv (k \text{ says } s) \text{ true}
$$

```math
\begin{align}
\underline{\quad \Sigma \vdash k ≽ k_0 \quad \quad}\\
\Sigma; \Gamma, k \text{ claims } s\vdash^{k_0} s
\end{align}
```

where ≽ gives a partial order of principals. $\Sigma$ is the context of typed variables and $\Gamma$ the context of Judgements such as $s \text{ true }$ or $k \text{ claims } s$.

What this shows is how we use rdf datasets, which consist of a default graph, and a set of named graphs. The default graph is the one that has a hidden implicit agent pronouncing it, so it matches very nicely.

This leads to the "claims principle. Having defined we defined an operator $\Gamma|$ that restricts the hypothesis $\Gamma$ to the claims of principals.
```math
\Gamma| = \{(k′ \text{ claims } s) ∈ \Gamma\}
```

Deepak Garg defines the claims principle which informally states that f we can establish $s$ true in the view k from only the claims of other principals, and from that another principal $k'$ can establish $s'$ true from the same context with the additional claim that k holds s true, then that new principal $k_0$ can hold s' true, without reference to k.

$\Sigma; \Gamma| \vdash^{k} s$ **and** $\Sigma; \Gamma, k \text{ claims } s \vdash^{k_0} s'$ **implies** $\Gamma \vdash^{k_0} s'$

## Mapping to RDF

One place to start the mapping is by looking at Evan Patterson's 2017 [Knowledge Representation in Bicategories of Relations](https://arxiv.org/abs/1706.00526) whose appendix comes with a sequent calculus for RDF. 
The modal part would then need to be added.

