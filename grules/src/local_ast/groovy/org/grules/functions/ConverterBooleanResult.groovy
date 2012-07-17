package org.grules.functions


class ConverterBooleanResult {
	
   final boolean value
	 
	 static ConverterBooleanResult wrap(Boolean value) {
		 new ConverterBooleanResult(value)
	 }
	 
	 static wrap(value) {
		 value
	 }
	 
	 private ConverterBooleanResult(value) {
		 this.value = value
	 }
}
