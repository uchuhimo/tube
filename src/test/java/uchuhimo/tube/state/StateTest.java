package uchuhimo.tube.state;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import uchuhimo.tube.TubeSession;
import uchuhimo.tube.value.primitive.MutableInt;
import uchuhimo.tube.value.tuple.Tuple2;

import org.junit.Test;

import java.util.Map;

public class StateTest {
  @Test
  public void testNewIntState() throws Exception {
    final TubeSession session = TubeSession.newInstance();
    final StateRef<MutableInt> intState = session.newInt();
    assertThat(intState.getSessionId(), is(session.getId()));
    assertThat(intState, is(session.getStateById(intState.getId())));
    final MutableInt mutableInt = intState.getStateFactory().newState(() -> 0);
    assertThat(mutableInt.get(), is(0));
  }

  @Test
  public void testNewTuple2State() throws Exception {
    final TubeSession session = TubeSession.newInstance();
    final StateRef<Tuple2<MutableInt, Map<Integer, Double>>> tupleState =
        session.newTuple2(session.newInt(), session.<Integer, Double>newMap());
    final Tuple2<MutableInt, Map<Integer, Double>> tuple = tupleState.getStateFactory().newState(() -> 0);
    assertThat(tuple.getElement1().get(), is(0));
    assertThat(tuple.getElement2().entrySet(), empty());
  }

  @Test
  public void testNewCustomStateByFactory() throws Exception {
    final TubeSession session = TubeSession.newInstance();
    final StateRef<Person> personState = session.newBy(context -> new Person("name", context.getPartitionId()));
    final Person person = personState.getStateFactory().newState(() -> 1);
    assertThat(person.name, is("name"));
    assertThat(person.age, is(1));
  }

  @Test
  public void testNewCustomStateByContextFreeFactory() throws Exception {
    final TubeSession session = TubeSession.newInstance();
    final StateRef<Person> personState = session.newBy(Person::new);
    final Person person = personState.getStateFactory().newState(() -> 1);
    assertThat(person.name, is("default"));
    assertThat(person.age, is(0));
  }

  @Test
  public void testNewCustomStateByClass() throws Exception {
    final TubeSession session = TubeSession.newInstance();
    final StateRef<Person> personState = session.newBy(Person.class);
    final Person person = personState.getStateFactory().newState(() -> 1);
    assertThat(person.name, is("default"));
    assertThat(person.age, is(0));
  }

  private static class Person {
    public String name;
    public int age;

    public Person() {
      this("default", 0);
    }

    public Person(String name, int age) {
      this.name = name;
      this.age = age;
    }
  }
}
