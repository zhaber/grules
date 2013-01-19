package test

import org.grules.Grules

class Test {

  public static void main(String[] s) {
    def grules = new Grules()
    def result = grules.applyRules(HelloGrules, [email: "megmail.com", age: "35"])
    assert result.cleanParameters.age == 35
    assert "email" in result.invalidParameters
    assert result.invalidParameters.email.errorId == "Invalid email"
    println result
  }
}
