package org.example;

import java.util.List;
import java.util.function.Consumer;

import org.example.exception.TestException;
import org.example.util.CheckedConsumer;

import static java.util.Collections.singletonList;

public class Main {

  @SuppressWarnings("unused")
  public static void main(String[] args) {
    // doStuff throws a "TestException". That exception is a checked exception.
    // Since the consumer interface does not allow a checked exception to be thrown
    // it has to be caught within the Consumer implementation.
    // Because I don't want to do that every time I have created the CheckedConsumer interface
    // which does exactly that. It catches any exception and wraps it in a RuntimeException.

    // Using that CheckedConsumer interface works fine here.
    Consumer<String> consumer = CheckedConsumer.wrap(s -> doStuff(s, "bar"));

    // Wrapping the Consumer in a List works fine too
    List<Consumer<String>> consumerInList =
        singletonList(CheckedConsumer.wrap(s -> doStuff(s, "bar")));

    // Wrapping that List with the consumer again causes a compile error telling me
    // that the consumer can throw a "TestException".
    // For some reason the compiler does not understand that the CheckedConsumer#wrap method
    // wraps any exception and therefore no checked exception will be thrown by the consumer.
    // From my observation this is not bound to the List interface. Wrapping the "Consumer<String>"
    // at least two times with some other generics causes the compiler to throw that error.

    //    List<List<Consumer<String>>> consumerInListInList =
    //        singletonList(singletonList(CheckedConsumer.wrap(s -> doStuff(s, "bar"))));

    // To prove that this is not bound to List interface
    // I will use the simple class Pair and a nest a consumer twice.

    // Pair<Pair<Consumer<String>, String>, String> consumerInPairInPair =
    //   Pair.of(Pair.of(CheckedConsumer.wrap(s -> doStuff(s, "bar")), "foo"), "bar");

    // The compiler works again if you use multiple variables.
    // For example if you define the consumer directly in a variable it works fine.
    List<List<Consumer<String>>> consumerInListInListWorking =
        singletonList(singletonList(consumer));

    // Even having a list of a consumer in a variable works fine.
    List<List<Consumer<String>>> consumerInListInListWorking2 = singletonList(consumerInList);

    // I am very confused why this is not working... Maybe a bug during type inference?
    // When expanding the lambda statement to the anonymous class it presents everything works fine
    List<List<Consumer<String>>> consumerInListInListWithAnonymousClass =
        singletonList(
            singletonList(
                CheckedConsumer.wrap(
                    new CheckedConsumer<String, TestException>() {
                      @Override
                      public void accept(String s) throws TestException {
                        doStuff(s, "bar");
                      }
                    })));
    // From here we can reduce the anonymous class step by step towards the lambda expression and
    // see where the compilation breaks.
    List<List<Consumer<String>>> consumerInListInListStep1 =
        singletonList(
            singletonList(
                CheckedConsumer.wrap(
                    (String s) -> {
                      doStuff(s, "bar");
                    })));
    List<List<Consumer<String>>> consumerInListInListStep2 =
        singletonList(singletonList(CheckedConsumer.wrap((String s) -> doStuff(s, "bar"))));

    // As soon as we arrive the minimal lambda representation the compilation breaks again :(
    //    List<List<Consumer<String>>> consumerInListInListWithMinimalLambda =
    //        singletonList(singletonList((CheckedConsumer.wrap(s -> doStuff(s, "bar")))));

    // Casting the lambda statement works fine
    List<List<Consumer<String>>> consumerInListInListWithMinimalLambdaCasted =
        singletonList(
            singletonList(
                CheckedConsumer.wrap(
                    (CheckedConsumer<String, TestException>) s -> doStuff(s, "bar"))));

    // Even a method reference works fine
    List<List<Consumer<String>>> consumerInListInListWithMethodReference =
        singletonList(singletonList(CheckedConsumer.wrap(Main::doStuffWithOneParameter)));

    // Another observation: When removing the bound type parameter `E` from the function
    // CheckedConsumer#wrap
    // and replacing it with a wildcard the previous stated breaking examples seem to work fine..

    // tested JDKs:
    // JDK                     | compiles?
    // adopt-openj9-11.0.11    | no
    // adopt-openj9-11.0.11    | no
    // adopt-openj9-13.0.2     | no
    // adopt-openj9-14.0.2     | no
    // adopt-openj9-15.0.2     | no
    // adopt-openj9-16         | no
    // adopt-openj9-1.8.0_292  | yes
    // adopt-openjdk-13.0.2    | no
    // adopt-openjdk-14.0.2    | no
    // adopt-openjdk-15.0.2    | no
    // adopt-openjdk-16.0.2    | no
    // adopt-openjdk-1.8.0_302 | yes
    // azul-11.0.9.1           | no
    // azul-13.0.5.1           | no
    // azul-15.0.4             | no
    // azul-16.0.2             | no
    // azul-1.8.0_302          | yes
    // corretto-11.0.12        | no
    // corretto-15.0.2         | no
    // corretto-16.0.2         | no
    // corretto-1.8.0_302      | yes
    // liberica-11.0.12        | no
    // liberica-16.0.2         | no
    // liberica-1.8.0_302      | yes
    // sapmachine-11.0.12      | no
    // sapmachine-16.0.2       | no
    // java-11-adoptopenjdk    | no
    // java-11-openjdk         | no
    // java-16-openjdk         | no
    // java-8-jdk              | yes
    // zulu-14                 | no
    // More observations: This class does not compile with any jdk 11+ and the compilation issue
    // exists across multiple JDK implementations. This leads me to believe that there is a
    // specification limitation introduced in JDK11+. I am not sure tho.

    System.out.println("main method ran successfully");
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
