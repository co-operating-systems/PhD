Deepak Garg's 2009 Ph.D. dissertation [Proof Theory for Authorization Logic and Its Application to a Practical File System](https://people.mpi-sws.org/~dg/papers/papers.html#theses) which uses updated `says` logic to apply it to Proof Carrying Authorizations, comes with a few examples that we can try to translate to our Solid web framework.

- [Access Control Lists](#access-control-lists)


# Access Control Lists 

The thesis starts with the simplest Authorization Policy: Access Control Lists. ACL authorization clearly separates the process of Authentication and Authorization. The Guard on the server (a.k.a a Reference Monitor) can check for any resource and find out if a particular principal can access that resource in the given mode.
This can be implemented as an exhaustive list of atomic statement,
which would then be easy to index and to search.

This is also nicely explained in Martin Abadi's 2009 [Logic in Access Control Tutorial Notes](https://users.soe.ucsc.edu/~abadi/Papers/fosad-acllogic.pdf) ([Springer](https://link.springer.com/chapter/10.1007/978-3-642-03829-7_5)). All one needs is a three place predicate to define atomic statements, so that one can define
that Alice may read `foo.txt` as: 

$$
\text{may-access}(\text{Alice}, \text{Foo.txt}, \text{Rd})
$$


Current implementations of [Web Access Control](https://solidproject.org/TR/wac) use the acl ontology with 
```turtle
@prefix acl: <http://www.w3.org/ns/auth/acl#> .
```
in a way that is close to the usual understanding of Access Control List's. We want to show here that the acroynym "acl" should be understood as "access control logic", and that the ontology there can be the basis of the use cases described in the 





