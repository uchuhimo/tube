package uchuhimo.tube.state;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import uchuhimo.tube.TubeSession;
import uchuhimo.tube.value.primitive.MutableInt;
import uchuhimo.tube.value.tuple.Tuple2;

import org.eclipse.collections.impl.factory.Lists;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class StateTest {
  private static final StateFactory.Context context = new StateFactory.Context() {
    @Override
    public int getPartitionId() {
      return 1;
    }

    @Override
    public <TState> TState getState(StateRef<TState> ref) {
      return ref.getFactory().newState(context);
    }
  };

  @Test
  public void testNewIntState() throws Exception {
    final TubeSession session = TubeSession.newInstance();
    final StateRef<MutableInt> intState = session.newInt();
    assertThat(intState.getRepoId(), is(session.getId()));
    assertThat(intState, is(session.getStateRefById(intState.getStateId())));
    final MutableInt mutableInt = intState.getFactory().newState(context);
    assertThat(mutableInt.get(), is(0));
  }

  @Test
  public void testNewTuple2State() throws Exception {
    final TubeSession session = TubeSession.newInstance();
    final StateRef<Tuple2<MutableInt, Map<Integer, Double>>> tupleState =
        session.newTuple2(session.newInt(), session.<Integer, Double>newMap());
    final Tuple2<MutableInt, Map<Integer, Double>> tuple =
        tupleState.getFactory().newState(context);
    assertThat(tuple.getElement1().get(), is(0));
    assertThat(tuple.getElement2().entrySet(), empty());
  }

  @Test
  public void testNewCustomStateByFactory() throws Exception {
    final TubeSession session = TubeSession.newInstance();
    final StateRef<Person> personState = session.newBy(context -> new Person("name", context.getPartitionId()));
    final Person person = personState.getFactory().newState(context);
    assertThat(person.name, is("name"));
    assertThat(person.age, is(1));
  }

  @Test
  public void testNewCustomStateByContextFreeFactory() throws Exception {
    final TubeSession session = TubeSession.newInstance();
    final StateRef<Person> personState = session.newBy(Person::new);
    final Person person = personState.getFactory().newState(context);
    assertThat(person.name, is("default"));
    assertThat(person.age, is(0));
  }

  @Test
  public void testNewCustomStateByClass() throws Exception {
    final TubeSession session = TubeSession.newInstance();
    final StateRef<Person> personState = session.newBy(Person.class);
    final Person person = personState.getFactory().newState(context);
    assertThat(person.name, is("default"));
    assertThat(person.age, is(0));
  }

  @Test
  public void testNewCustomCompositeStateByFactory() throws Exception {
    final TubeSession session = TubeSession.newInstance();
    final StateRef<MutableInt> depositState = session.newInt();
    final StateRef<Person> personState = session.newBy(Person::new);
    final StateRef<Account> accountState = session.newBy(new CompositeStateFactory<Account>() {
      @Override
      public List<StateRef<?>> getRefs() {
        return Lists.fixedSize.of(depositState, personState);
      }

      @Override
      public Account newStateWithRefs(Context context, List<?> refs) {
        final MutableInt deposit = (MutableInt) refs.get(0);
        deposit.set(3);
        final Person person = (Person) refs.get(1);
        person.age = 12;
        person.name = "owner";
        return new Account(deposit, person);
      }
    });
    final Account account = accountState.getFactory().newState(context);
    assertThat(account.deposit.get(), is(3));
    assertThat(account.owner.age, is(12));
    assertThat(account.owner.name, is("owner"));
  }

  @Test
  public void testNewCustomElement2StateByFactory() throws Exception {
    final TubeSession session = TubeSession.newInstance();
    final StateRef<MutableInt> depositState = session.newInt();
    final StateRef<Person> personState = session.newBy(Person::new);
    final StateRef<Account> accountState =
        session.newComposite(depositState, personState, Account::new);
    final Account account = accountState.getFactory().newState(context);
    assertThat(account.deposit.get(), is(0));
    assertThat(account.owner.age, is(0));
    assertThat(account.owner.name, is("default"));
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

  private static class Account {
    public MutableInt deposit;
    public Person owner;

    public Account(MutableInt deposit, Person owner) {
      this.deposit = deposit;
      this.owner = owner;
    }
  }
}
