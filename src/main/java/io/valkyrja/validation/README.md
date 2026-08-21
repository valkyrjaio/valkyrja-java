# Validation

## Introduction

The validation component declares the shape of a rule and of a validator. The
port holds the two contracts. It ships no rule, and it ships no validator, so an
application supplies both.

The component publishes no container binding, and it holds no configuration.

## The rule contract

`io.valkyrja.validation.rule.contract.RuleContract` declares no method.

```java
public interface RuleContract {}
```

A rule is a type, and the type is what a caller reads. An application declares
one class for each rule, and each class implements the contract.

## The validator contract

`io.valkyrja.validation.validator.contract.ValidatorContract` declares one
method.

```java
public interface ValidatorContract {

    boolean validateRules();
}
```

The contract holds one abstract method, so a lambda satisfies it.

## Where the framework reads the contracts

An HTTP request struct holds the rules for one route.
`io.valkyrja.http.struct.request.contract.RequestStructContract` declares two
methods that read this component.

```java
Map<String, List<RuleContract>> getValidationRules(ServerRequestContract request);

ValidatorContract validate(ServerRequestContract request);
```

`io.valkyrja.http.struct.request.abstract_.RequestStruct` implements both.
`getValidationRules` returns an empty map, and `validate` returns a lambda that
reads the map.

`RequestStructMiddleware` calls `validate(request).validateRules()` for a matched
route that holds a request struct. A `false` result returns a 400 response, and
the route handler does not run. The [http component](../http/README.md)
describes the middleware.

Warning: `RequestStruct.validateAllRules` returns `true` for every map of rules,
so the framework evaluates no rule. A struct that returns rules from
`getValidationRules` also overrides `validateAllRules`, and the override
evaluates them.

```java
@Override
protected boolean validateAllRules(Map<String, List<RuleContract>> rules) {
    // Evaluate each rule for each key, and return the result.
}
```
