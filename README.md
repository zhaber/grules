Grules is a rule engine for data preprocessing. The rules are specified via internal Groovy DSL, which has a concise and simple syntax. For example:

```java
// isEmail is a Groovy/Java method that takes an email value as its parameter
email isEmail ["Invalid email"]

// invalidLoginMessage and dupLoginMessage are String error messages
login isLogin [invalidLoginMessage] >> isUnique [dupLoginMessage] 

// The value gender defaults to "MALE"
gender["MALE"] toEnum(Gender) 

// agreeToTerms is a message from a resource bundle
termsCondition[""] !isEmpty [m.agreeToTerms] 

// you can use closures as well
weight toPositiveBigDecimal [decimalErr] >> {round(it / 1000)} 

// Grules supports logical operators 
endDate isAfterNow && isBefore(deadline) && {it.day != 1}
```
To build the project, you should run the following command in the grules folder:

    cd grules
    ./gradlew

To run hello world example:

    cd grulesHelloWorld
    ./gradlew
    
Documentation:

<a href="https://github.com/zhaber/grules/wiki">Wiki documentation</a><br>
<a href="http://digitalcommons.mcmaster.ca/cgi/viewcontent.cgi?article=8244&context=opendissertations">White paper</a><br>
<a href="http://www.youtube.com/watch?v=6RYbDRY6cvQ">Introduction video</a><br>
<a href="http://zhaber.github.io/grules/">Home page</a><br>

Requirements: JVM 8, Groovy 2.3
