Grules is a rule engine for data preprocessing. The rules are specified via internal Groovy DSL, which has a concise and simple syntax. For example:

```java
// isEmail is a Groovy/Java method that takes an email value as its parameter
email isEmail ["Invalid email"]

// invalidLoginErr and dupLoginErr are String error messages
login isLogin [invalidLoginErr] >> isUnique [dupLoginErr] 

// Gender is a Groovy/Java enumeration
gender toEnum(Gender) 

// zip code defaults to 10001
zipcode["10001"] isValidZipcode 

// agreeToTerms is a message from a resource bundle
termsCondition[""] !isEmpty [m.agreeToTerms] 

// you can use closures as well
weight toPositiveBigDecimal [decimalErr] >> {round(it / 1000)} 

// Grules supports logical operators 
endDate isAfterNow && isBefore(deadline) && {it.day != 1}
```
To build the project, you should run the following command in the grules folder:

    ./gradlew

Requirements: JVM 6, Groovy 2.0.4

<a href="https://github.com/zhaber/grules/wiki">Wiki documentation</a><br>
<a href="http://digitalcommons.mcmaster.ca/cgi/viewcontent.cgi?article=8244&context=opendissertations">Paper</a><br>
<a href="http://www.youtube.com/watch?v=6RYbDRY6cvQ">Introduction video</a><br>
