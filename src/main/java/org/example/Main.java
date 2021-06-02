package org.example;


import java.util.List;
import java.util.function.Consumer;
import org.example.exception.TestException;
import org.example.util.CheckedConsumer;

public class Main {

  public static void main(String[] args) {
    // doStuff throws a "TestException". That exception is a checked exception.
    // Since the consumer interface does not allow a checked exception to be thrown
    // it has to be caught within the Consumer implementation.
    // Because I don't want to do that every time I have created the CheckedConsumer interface
    // which does exactly that. It catches any exception and wraps it in a RuntimeException.

    // Using that CheckedConsumer Interface works fine here.
    Consumer<String> consumer = CheckedConsumer.wrap(s -> doStuff(s, "bar"));

    // Wrapping the Consumer in a List works fine too
    List<Consumer<String>> consumerInList = List.of(CheckedConsumer.wrap(s -> doStuff(s, "bar")));

    // Wrapping that List with the consumer again causes a compile error telling me
    // that the consumer can throw a "TestException".
    // For some reason the compiler does not understand that the CheckedConsumer#wrap method
    // wraps any exception and therefore no checked exception will be thrown by the consumer.
    // From my observation this is not bound to the List interface. Wrapping the "Consumer<String>"
    // at least two times with some other generics causes the compiler to throw that error.

    // List<List<Consumer<String>>> consumerInListInList = List.of(List.of(CheckedConsumer.wrap(s -> doStuff(s, "bar"))));

    // To prove that this is not bound to List I will use the simply class Pair and a nest a consumer twice.

    // Pair<Pair<Consumer<String>, String>, String> consumerInPairInPair =
    //   Pair.of(Pair.of(CheckedConsumer.wrap(s -> doStuff(s, "bar")), "foo"), "bar");

    // The compiler works again if you use multiple variables.
    // For example if you define the consumer directly in a variable it works fine.
    List<List<Consumer<String>>> consumerInListInListWorking = List.of(List.of(consumer));

    // Even having a list of a consumer in a variable works fine.
    List<List<Consumer<String>>> consumerInListInListWorking2 = List.of(consumerInList);

    // I am very confused why this is not working... Maybe a bug during type inference?

    System.out.println("main method exit");
  }

  public static void doStuff(String left, String right) throws TestException {
    if (left.equals("EXCEPTION")) {
      throw new TestException();
    }
    System.out.println("STUFF: " + left + " " + right);
  }


}
