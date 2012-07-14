package grules

HEADER:

referer startsWith("http://localhost")

PARAMETERS:

login isAlphanum ["Login has to be an alphanumeric value"]

password isStrongPassword([/.*\w.*/, /.*\d.*/, /.{5,}/], [/.*\+.*/])

age toInt >> {it > 18}

beginDate isAfterNow

endDate isAfter(beginDate)