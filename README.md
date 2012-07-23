Grules is a rule engine for data preprocessing (validation and canonicalization). The rules are specified via internal Groovy DSL, which has a concise and simple syntax. For example:

```java
// isEmail is a Groovy method that takes an email value as its parameter
email isEmail ["Invalid email"]

// invalidLoginErr and dupLoginErr are String error messages
login isLogin [invalidLoginErr] >> isUnique [dupLoginErr] 

// Gender is a Groovy enumeration
gender toEnum(Gender) 

// "10001" is a default value
zipcode["10001"] isValidZipcode 

// agreeToTerms is a message from a resource bundle
termsCondition[""] !isEmpty [m.agreeToTerms] 

// you can use closures as well
weight toPositiveBigDecimal [decimalErr] >> {round(it / 1000)} 

// Grules supports disjunction, conjunction, negation
endDate isAfterNow && isBefore(deadline)
```

To build the project, you should run the following command in the grules folder:

    ./gradlew build
