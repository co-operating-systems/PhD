From Access Control Lists to AC Logic.

# Access Control Lists 

The simplest Authorization Policy is Access Control Lists. It separates the process of Authentication and Authorization. The Guard on the server (a.k.a a Reference Monitor) can check for any resource and find out if a particular principal can access that resource in the given mode.
This can be implemented as an exhaustive list of atomic ground statements, which would then be easy to index and search, but as we will see not to maintain.

This is nicely explained in Martin Abadi's 2009 [Logic in Access Control Tutorial Notes](https://users.soe.ucsc.edu/~abadi/Papers/fosad-acllogic.pdf) ([Springer](https://link.springer.com/chapter/10.1007/978-3-642-03829-7_5)). All one needs is a three-place predicate $\text{may-access}(p, r, m)$ to define atomic statements, so that one can define that Alice may read `foo.txt` as: 

$$
\text{may-access}(\text{Alice}, \text{Foo.txt}, \text{Rd})
$$

Or one could even have three binary predicates `read`, `write`, `create`, which would work well RDF.

```Turtle
:alice acl:read <Foo> .
:alice acl:write <Foo> .
```

## Problems with AC Lists

Deepak Garg's 2009 Ph.D. dissertation [Proof Theory for Authorization Logic and Its Application to a Practical File System](https://people.mpi-sws.org/~dg/papers/papers.html#theses) lists the following reasons for a purely fact-based representation such as
the above:
1. they do not carry information about why access is allowed
2. it is therefore difficult to update the data

Consider that Alice may be allowed read access to `</secret/level2/>` because she is a manager with level 2 clearance. The facts could be expressed as

```turtle
:alice acl:read </secret/level2/> .
```

But that does not contain the reason why it is true.

If later her clearance level is reduced to level 1, the simple statement of fact in the database does not indicate that it needs to be changed. Furthermore, no log trace can be kept as to why access was granted either.

**Furthermore** in a decentralized system, one cannot know all the people that may want to access a resource. 
* One may want people from other companies that have a specific relation to one's company to access. 
* Or perhaps the resource can be accessed by anyone that has paid for it. Of course, as soon as someone has paid, one could update the access control rules with the fact of giving that person access. But then one would be back to the previous problems of not knowing why those people have access.
* Finally one may want to allow access to people that can present a token useable for 3 accesses. That type of access could not be expressed in that simple ACL.

# Adding Rules gives Access Control Logic

How far can one go with just this, if we then add rules?

One can give access by role, such as the following rule that allows only a Manager above level 2 to see the `<secret>` document.

$$
\forall x. \text{ Manager}(x) \land \text{level}(x) \geqslant 2 \rightarrow \text{CanRead}(x, \texttt{<secret>}) 
$$

The advantage of this rule is that it would not require deleting all the individual facts when someone's role changed in the company.

### A typesafe version

Rather than give access to individual documents that way one could give access to all documents with a certain label.
In a more typesafe notation [^1] we could write the rule that uses a function $\text{level}$ that takes a manager or resource to a level represented as a natural number.

$$
level : \text{Manager} + \text{Resource} \to \Bbb{N} \\
\forall m: \text{Manager}, r: \text{Resource}.\space \text{level}(m) \geqslant \text{level}(r) \rightarrow \text{CanRead}(x, r) 
$$
Then given the following facts
$$
\newcommand{\x}{\text}
\x{alice}: \x{Manager} \\
\x{level}(\x{alice}) = 2 \\
\x{level}(\texttt{</secret/doc>}) = 2 \\
$$

With that, we can build a proof that 
$$\text{CanRead}(\text{alice}, \texttt{</secret/doc>})$$

### Using OWL DL

What we were doing above with typed logic could also be
done with Description Logic. We can think of a rule as
linking a set of resources and a set of agents via description.

```turtle
@prefix acl: <http://www.w3.org/ns/auth/acl#> .
@prefix owl: <http://www.w3.org/2002/07/owl#> .

<#allowCleared> a acl:Authorization;
    acl:modes acl:Read;
    acl:accessToClass [ a owl:Restriction;
        owl:onProperty security:clearance;
        owl:hasValue 2 ];     
    acl:agentClass [ a owl:Restriction;
        owl:onProperty :clearance;
        owl:hasValue 2 ].
```

That is a rule that says that if the clearance of the agent is 2 and the clearance of the resource is 2, then the agent can read the resource.  With OWL we cannot (easily?) link the two values so that one can find the relation specifying that the agent's clearance must be greater than or equal to the resource's clearance.

On the other hand, OWL can give us some complexity guarantees.
todo: How much are complexity guarantees of OWL needed if we want the client, in the end, to pass a proof to the server?


## Using N3

N3 gives us first-order logic, so we could write the rule as

```n3
@prefix acl: <http://www.w3.org/ns/auth/acl#> .

{ ?agent acl:clearance ?agentClearance .
  ?resource acl:clearance ?resourceClearance .
  ?agentClearance >= ?resourceClearance .
} => {
  [] a acl:Authorization;
    acl:agent ?agent;
    acl:mode acl:read; 
    acl:accessTo ?resource .
}
```






[^1] [On Type Distinctions and Expressivity](https://twitter.com/DavidCorfield8/status/1628815276702670850) but also [the Tweet by David Corfield](https://twitter.com/DavidCorfield8/status/1628815276702670850) and an interesting point about [presuppositions and consequences](https://twitter.com/DavidCorfield8/status/1689631794604523523)










