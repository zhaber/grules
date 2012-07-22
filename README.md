Grules is a rule engine for data preprocessing (validation and canonicalization). The rules are specified via internal Groovy DSL, which has a concise and simple syntax. For example:

```groovy
email isEmail ['Not valid email'] // isEmail is a Groovy method that takes an email value as its parameter

login isLogin [invalidLoginErr] >> isUnique [dupLoginErr] // invalidLoginErr and dupLoginErr are String error messages

gender toEnum(Gender) // Gender is a Groovy enumeration

zipcode['10001'] isValidZipcode // '10001' is a default value

termsCondition[''] !isEmpty [m.agreeToTerms] // agreeToTerms is a message from a resource bundle

weight toPositiveBigDecimal [decimalErr] >> {round(it / 1000)} // you can use closures as well

endDate isAfterNow && isBefore(deadline) // Grules supports disjunction, conjunction, negation
```

To build the project, you should run the following command in the grules folder:

    ./gradlew build
