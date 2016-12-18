package uchuhimo.tube.state;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import uchuhimo.tube.state.util.Mock;
import uchuhimo.tube.value.primitive.MutableDouble;
import uchuhimo.tube.value.primitive.MutableInt;
import uchuhimo.tube.value.primitive.MutableLong;
import uchuhimo.tube.value.primitive.MutableString;
import uchuhimo.tube.value.tuple.Tuple2;
import uchuhimo.tube.value.tuple.Tuple3;

import org.eclipse.collections.impl.factory.Lists;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class StateRepoTest {

  @Test
  public void testRegisterStateToRepo() throws Exception {
    final StateRepo stateRepo = Mock.newStateRepo();
    assertThat(stateRepo.getId(), is(0));
    assertThat(stateRepo.getDefaultPartitionCount(), is(1));
    final StateRef<MutableInt> intState = stateRepo.newInt();
    final PhaseGroup<MutableInt> phases = intState.phaseBy(2);
    assertThat(stateRepo.getRootRefs(), contains(intState));
    assertThat(stateRepo.getStateRef(intState.getStateId()), is(intState));
    assertThat(
        stateRepo.getStateRef(phases.getPhases().get(0).getStateId()),
        is(phases.getPhases().get(0)));
    assertThat(
        stateRepo.getStateRef(phases.getPhases().get(1).getStateId(), MutableInt.class),
        is(phases.getPhases().get(1)));
  }

  @Test
  public void testNewIntState() throws Exception {
    final StateRepo stateRepo = Mock.newStateRepo();
    final StateRef<MutableInt> intState = stateRepo.newInt();
    final MutableInt mutableInt = intState.getFactory().newState(Mock.newStateFactoryContext());
    assertThat(mutableInt.get(), is(0));
  }

  @Test
  public void testNewLongState() throws Exception {
    final StateRepo stateRepo = Mock.newStateRepo();
    final StateRef<MutableLong> longState = stateRepo.newLong();
    final MutableLong mutableLong = longState.getFactory().newState(Mock.newStateFactoryContext());
    assertThat(mutableLong.get(), is(0L));
  }

  @Test
  public void testNewDoubleState() throws Exception {
    final StateRepo stateRepo = Mock.newStateRepo();
    final StateRef<MutableDouble> doubleState = stateRepo.newDouble();
    final MutableDouble mutableDouble =
        doubleState.getFactory().newState(Mock.newStateFactoryContext());
    assertThat(mutableDouble.get(), is(0.0));
  }

  @Test
  public void testNewMapState() throws Exception {
    final StateRepo stateRepo = Mock.newStateRepo();
    final StateRef<Map<Integer, Long>> mapState = stateRepo.newMap(Integer.class, Long.class);
    final Map<Integer, Long> map = mapState.getFactory().newState(Mock.newStateFactoryContext());
    assertThat(map.entrySet(), empty());
  }

  @Test
  public void testNewTuple2State() throws Exception {
    final StateRepo stateRepo = Mock.newStateRepo();
    final StateRef<Tuple2<MutableInt, Map<Integer, Double>>> tupleState =
        stateRepo.newTuple2(stateRepo.newInt(), stateRepo.<Integer, Double>newMap());
    final Tuple2<MutableInt, Map<Integer, Double>> tuple =
        tupleState.getFactory().newState(Mock.newStateFactoryContext());
    assertThat(tuple.field1().get(), is(0));
    assertThat(tuple.field2().entrySet(), empty());
  }

  @Test
  public void testNewTuple3State() throws Exception {
    final StateRepo stateRepo = Mock.newStateRepo();
    final StateRef<Tuple3<MutableInt, MutableLong, MutableDouble>> tupleState =
        stateRepo.newTuple3(stateRepo.newInt(), stateRepo.newLong(), stateRepo.newDouble());
    final Tuple3<MutableInt, MutableLong, MutableDouble> tuple =
        tupleState.getFactory().newState(Mock.newStateFactoryContext());
    assertThat(tuple.field1().get(), is(0));
    assertThat(tuple.field2().get(), is(0L));
    assertThat(tuple.field3().get(), is(0.0));
  }

  @Test
  public void testNewCustomStateByFactory() throws Exception {
    final StateRepo stateRepo = Mock.newStateRepo();
    final StateRef<Person> personState =
        stateRepo.newBy(context -> new Person("name", context.getPartitionId()));
    final Person person = personState.getFactory().newState(Mock.newStateFactoryContext());
    assertThat(person.name, is("name"));
    assertThat(person.age, is(1));
  }

  @Test
  public void testNewCustomStateByContextFreeFactory() throws Exception {
    final StateRepo stateRepo = Mock.newStateRepo();
    final StateRef<Person> personState = stateRepo.newBy(Person::new);
    final Person person = personState.getFactory().newState(Mock.newStateFactoryContext());
    assertThat(person.name, is("default"));
    assertThat(person.age, is(0));
  }

  @Test
  public void testNewCustomStateByClass() throws Exception {
    final StateRepo stateRepo = Mock.newStateRepo();
    final StateRef<Person> personState = stateRepo.newBy(Person.class);
    final Person person = personState.getFactory().newState(Mock.newStateFactoryContext());
    assertThat(person.name, is("default"));
    assertThat(person.age, is(0));
  }

  @Test
  public void testNewCustomCompositeStateByFactory() throws Exception {
    final StateRepo stateRepo = Mock.newStateRepo();
    final StateRef<MutableInt> depositState = stateRepo.newInt();
    final StateRef<Person> personState = stateRepo.newBy(Person::new);
    final StateRef<Account> accountState = stateRepo.newBy(new CompositeStateFactory<Account>() {
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
    final Account account = accountState.getFactory().newState(Mock.newStateFactoryContext());
    assertThat(account.deposit.get(), is(3));
    assertThat(account.owner.age, is(12));
    assertThat(account.owner.name, is("owner"));
  }

  @Test
  public void testNewCustomElement2StateByFactory() throws Exception {
    final StateRepo stateRepo = Mock.newStateRepo();
    final StateRef<MutableInt> depositState = stateRepo.newInt();
    final StateRef<Person> personState = stateRepo.newBy(Person::new);
    final StateRef<Account> accountState =
        stateRepo.newComposite(depositState, personState, Account::new);
    final Account account = accountState.getFactory().newState(Mock.newStateFactoryContext());
    assertThat(account.deposit.get(), is(0));
    assertThat(account.owner.age, is(0));
    assertThat(account.owner.name, is("default"));
  }

  @Test
  public void testNewCustomElement3StateByFactory() throws Exception {
    final StateRepo stateRepo = Mock.newStateRepo();
    final StateRef<MutableString> name = stateRepo.newString();
    final StateRef<MutableInt> age = stateRepo.newInt();
    final StateRef<MutableString> sex = stateRepo.newBy(() -> new MutableString("male"));
    final StateRef<PersonWithSex> personWithSexState =
        stateRepo.newComposite(name, age, sex, PersonWithSex::new);
    final PersonWithSex personWithSex =
        personWithSexState.getFactory().newState(Mock.newStateFactoryContext());
    assertThat(personWithSex.name.get(), is(""));
    assertThat(personWithSex.age.get(), is(0));
    assertThat(personWithSex.sex.get(), is("male"));
  }

  @Test
  public void testDumpRefs() throws Exception {
    final StateRepo stateRepo = Mock.newStateRepo();
    final StateRef<MutableInt> intState = stateRepo.newInt();
    final EnumPhaseGroup<MutableInt, OnePass> onePassGroup = intState.phaseBy(OnePass.class);
    onePassGroup.getPhase(OnePass.Update).phaseBy(2);
    final StateRef<MutableDouble> doubleState = stateRepo.newDouble();
    final StateRef<MutableString> stringState = stateRepo.newString();
    final StateRef<MutableLong> longState = stateRepo.newLong();
    final StateRef<Map<Integer, Long>> mapState = stateRepo.newMap(Integer.class, Long.class);
    stateRepo.newTuple2(doubleState, stringState);
    stateRepo.newTuple3(longState, mapState, intState);
    System.out.println(stateRepo.dumpRefs());
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

  private static class PersonWithSex {
    public MutableString name;
    public MutableInt age;
    public MutableString sex;

    public PersonWithSex(MutableString name, MutableInt age, MutableString sex) {
      this.name = name;
      this.age = age;
      this.sex = sex;
    }
  }
}
