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
    // When expanding the lambda statement to the anonymous class it presents everything works fine
    List<List<Consumer<String>>> consumerInListInListWithAnonymousClass = List
        .of(List.of(CheckedConsumer.wrap(new CheckedConsumer<String, TestException>() {
          @Override
          public void accept(String s) throws TestException {
            doStuff(s, "bar");
          }
        })));
    // From here we can reduce the anonymous class step by step towards the lambda and see where the compilation breaks.
    List<List<Consumer<String>>> consumerInListInListStep1 = List
        .of(List.of(CheckedConsumer.wrap((String s) -> {
              doStuff(s, "bar");
            }
        )));
    List<List<Consumer<String>>> consumerInListInListStep2 = List
        .of(List.of(CheckedConsumer.wrap((String s) -> doStuff(s, "bar")
        )));

    // As soon as we arrive the minimal lambda representation the compilation breaks again :(
//    List<List<Consumer<String>>> consumerInListInListWithMinimalLambda = List
//        .of(List.of(CheckedConsumer.wrap(s -> doStuff(s, "bar"))));


    // Casting the lambda statement works fine
    List<List<Consumer<String>>> consumerInListInListWithMinimalLambdaCasted = List
        .of(List.of(CheckedConsumer.wrap((CheckedConsumer<String, TestException>) s -> doStuff(s, "bar"))));

    // Even a method reference works fine
    List<List<Consumer<String>>> consumerInListInListWithMethodReference = List
        .of(List.of(CheckedConsumer.wrap(Main::doStuffWithOneParameter)));

    // Another observation: When removing the bound type parameter `E` from the function CheckedConsumer#wrap
    // and replacing it with a wildcard the previous stated breaking examples seem to work fine..

    System.out.println("main method exit");
  }

  public static void doStuff(String left, String right) throws TestException {
    if ("EXCEPTION".equals(left)) {
      throw new TestException();
    }
    System.out.println("STUFF: " + left + " " + right);
  }

  public static void doStuffWithOneParameter(String s) throws TestException {
    if ("EXCEPTION".equals(s)) {
      throw new TestException();
    }
    System.out.println("STUFF with one parameter: " + s);
  }

}
