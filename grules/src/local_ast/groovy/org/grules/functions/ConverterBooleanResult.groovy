package org.grules.functions

/**
 * A wrapper for a converter function return value. It is used to distinguish between validators and boolean converters.
 */
class ConverterBooleanResult {

   final Boolean value

   /**
    * Returns the passed value wrapped in ConverterBooleanResult.
    *
    * @param value
    * @return value wrapped in ConverterBooleanResult.
    */
   static ConverterBooleanResult wrap(Boolean value) {
     new ConverterBooleanResult(value)
   }

   /**
    * Returns the passed value as non-boolean values do not need wrapping.
    *
    * @param value
    * @return same value
    */
   static wrap(value) {
     value
   }

   /**
    * Constructs a wrapper for a result returned from of a boolean converter.
    *
    * @param value wrapped value
    */
   private ConverterBooleanResult(Boolean value) {
     this.value = value
   }
}