package edu.csc413.calculator.evaluator;



import edu.csc413.calculator.exceptions.InvalidTokenException;
import edu.csc413.calculator.operators.*;

import java.util.Stack;
import java.util.StringTokenizer;

public class Evaluator {

  private Stack<Operand> operandStack;
  private Stack<Operator> operatorStack;
  private StringTokenizer expressionTokenizer;
  private final String delimiters = " +/*-^()";

  public Evaluator() {
    operandStack = new Stack<>();
    operatorStack = new Stack<>();
  }

  public int evaluateExpression(String expression ) throws InvalidTokenException {
    String expressionToken;

    // The 3rd argument is true to indicate that the delimiters should be used
    // as tokens, too. But, we'll need to remember to filter out spaces.
    this.expressionTokenizer = new StringTokenizer( expression, this.delimiters, true );

    // initialize operator stack - necessary with operator priority schema
    // the priority of any operator in the operator stack other than
    // the usual mathematical operators - "+-*/" - should be less than the priority
    // of the usual operators



    while ( this.expressionTokenizer.hasMoreTokens() ) {
      // filter out spaces
      if ( !( expressionToken = this.expressionTokenizer.nextToken() ).equals( " " )) {
        // check if token is an operand
        if ( Operand.check( expressionToken )) {
          operandStack.push( new Operand( expressionToken ));
        } else {
          if ( ! Operator.check( expressionToken )) {
            throw new InvalidTokenException(expressionToken);
          }

          //add the begin parentheses to the operator stack
          if (expressionToken.equals("(")) {
            operatorStack.push(Operator.getOperator(expressionToken));
            continue;
          }
          //finds the ending parentheses operator to complete the computation inside before continuing
          //the beginning parentheses is popped at the end
          if(expressionToken.equals(")")) {
            while (operatorStack.peek().priority() > 0 ) {
              process();
            }
            operatorStack.pop();
            continue;
          }

          Operator newOperator =  Operator.getOperator(expressionToken);


          //To protect the while loop below from throwing an exception for empty operator stack
          try {
            while (operatorStack.peek().priority() >= newOperator.priority()) {
              // note that when we eval the expression 1 - 2 we will
              // push the 1 then the 2 and then do the subtraction operation
              // This means that the first number to be popped is the
              // second operand, not the first operand - see the following code
              Operator operatorFromStack = operatorStack.pop();
              Operand operandTwo = operandStack.pop();
              Operand operandOne = operandStack.pop();
              Operand result = operatorFromStack.execute(operandOne, operandTwo);
              operandStack.push(result);
            }
          } catch (Exception exception) {
          }

          operatorStack.push( newOperator );
        }
      }
    }


    // Control gets here when we've picked up all of the tokens; you must add
    // code to complete the evaluation - consider how the code given here
    // will evaluate the expression 1+2*3
    // When we have no more tokens to scan, the operand stack will contain 1 2
    // and the operator stack will have + * with 2 and * on the top;
    // In order to complete the evaluation we must empty the stacks,
    // that is, we should keep evaluating the operator stack until it is empty;
    // Suggestion: create a method that processes the operator stack until empty.

    while (!operatorStack.isEmpty() && operandStack.size()>=2) {
      process();
    }
    return operandStack.pop().getValue();
  }

  private void process() {
    Operand operandOne = operandStack.pop();
    Operand operandTwo = operandStack.pop();
    Operator operator = operatorStack.pop();
    operandStack.push(operator.execute(operandTwo, operandOne));

  }

}
