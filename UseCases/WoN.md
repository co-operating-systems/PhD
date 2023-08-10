
# Age claim

We may want to give access to resources tagged as "adult" to anyone who can prove that they are over 18. A further refinement would be to have the age vary per country and per action requested: in the USA the minimum drinking age is over 21 whereas in the UK it is 18, and the minimum age for driving is 16 in some states and 18 in others. (This may be getting more complicated than what we want to do here, but it is worth keeping in mind.)

For we will need a [Web Of Nations](https://co-operating.systems/2020/06/01/WoN/) that can let us know which organizations in which countries are legally entitled to make such claims for someone, and perhaps also which actions are legally allowed in that country for any citizen or for citizens of that country in any country. Clearly this cannot be something where self-claims are going to work. It would also be possible as a bootstrapping step to have a list maintained by some organization of age verification organizations across the globe. But that would create a centralization problem that is not correctly aligned with the geopolitical structure of the world, giving the maintainer of the list a lot more power than they should have. Still, both could work together.

Actually, with age, a number of organisations can be entitled to make such claims: schools, banks, the army, etc... If a given country does not allow a credit card to be given out to people under a certain age, then possessing a card in that country would be a proof of being over a certain age. Arguably they all rely on the birth certificate claim in the end, so these organizations would be just repeating claims from a canonical organization, such as the birth registrars in a country.

We should try to simplify the problem here in a way that allows for complexities to be added later. So let us assume that we have a canonical organization that can link to organizations in a number of countries that can make age claims. We will call that organization the `AgeRegistrarRegistrar``. 

## Age Rule

What would a rule look like? Well, we need a way to describe someone over a given age. Here is a proposal I made on [Jan 2021](https://github.com/solid/authorization-panel/issues/160#issuecomment-764722858)

```Turtle
<#PersonOver21> owl:equivalentClass [  a owl:Restriction;
      owl:onProperty :hasAge ;
      owl:someValuesFrom   
          [ rdf:type   rdfs:Datatype ;
            owl:onDatatype       xsd:integer ;
            owl:withRestrictions (  [ xsd:minExclusive     21 ]   [ xsd:maxInclusive    150 ] )
          ]
       ] .
```

(the max restriction is not needed, but we leave it there to illustrate the syntax)

Then we can create a rule

```Turtle
<#adultRule> a :Authorization;
  :mode :Read;
  :default </>;
  :agentClass <#PersonOver21>;
  :accessToClass [ a owl:Restriction;
      owl:onProperty :hasTag;
      owl:hasValue "adult" 
     ] .
```

So here we can imagine that resources are tagged in some way - perhaps we need a new `Tag` HTTP header, and that the rule allows access to any resource that has the tag `adult` if the agent is over 21.

Question: Is having the `:default` relation to a container needed here? 
Answer: In the current usage `:default` is a way of selecting all resources under the given container that do not themselves contain their own specified WAC resource. So here it should mean that we take that set and add the further restriction given by the `:accessToClass` relation. That seems correct as RDF graphs triples are conjunctions of statements. 

### Client Proof of Age

The client will need to check two things:

1. It will need to work out that the `<#adultRule>` applies to the resource it is trying to access. It could do that by checking that the resource has a header `Tag: adult` in it. It would know that if it received a 401 on making the request. As an optimization, we may want the resource to also link to a set of resources that are tagged that way, so that the client can avoid having to make requests leading to a 401 and can sign requests immediately. 

2. Having found that the rule applies, the client would then need to check if it can prove that it is a member of the class `<#PersonOver21>`.  If it has a credential that proves its age and that fits the description, then it could use that in providing the proof. 

### Server verification of Age

The Guard knowing that the requested resource is tagged "adult" will know that the rule `<#adultRule>` applies. In the simplest of cases, the key used by the client to sign the headers is the same key as the one referenced by the Verifiable Claim of Age signed by one of the recognized Age Registrars. More complex situations can occur where a chain of keys needs to be verified.

So to find the `:age` property of an agent, the Guard will need to find a claim of age made by a recognized registrar and then verify that the agent fits the given restriction. 

Todo: find an example of a Verifiable Claim of age, to illustrate the whole process.
