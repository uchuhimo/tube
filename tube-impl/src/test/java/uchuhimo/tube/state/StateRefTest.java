package uchuhimo.tube.state;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import uchuhimo.tube.state.util.Mock;
import uchuhimo.tube.value.primitive.MutableInt;
import uchuhimo.tube.value.tuple.Tuple;

import org.eclipse.collections.impl.factory.Lists;
import org.junit.Test;

public class StateRefTest {

  @Test
  public void testNewStateRef() throws Exception {
    final StateRepo stateRepo = Mock.newStateRepo();
    final StateRef<MutableInt> intState = stateRepo.newInt();
    assertThat(intState.getRepo(), is(stateRepo));
    assertThat(intState.getRepoId(), is(stateRepo.getId()));
    assertThat(intState.getPartitionCount(), is(stateRepo.getDefaultPartitionCount()));
    assertThat(intState.getStateId(), is(0));
    assertThat(intState.isBorrowed(), is(false));
    assertThat(intState.getPhases(), empty());
    final MutableInt mutableInt = intState.getFactory().newState(Mock.newStateFactoryContext());
    assertThat(mutableInt.get(), is(0));
  }

  @Test
  public void testPhaseByCount() throws Exception {
    final StateRef<MutableInt> intState = Mock.newStateRepo().newInt();
    final PhaseGroup<MutableInt> phaseGroup = intState.phaseBy(3);
    assertThat(intState.isBorrowed(), is(true));
    assertThat(intState.getPhases(), containsInAnyOrder(phaseGroup.getPhases().toArray()));
    assertThat(intState.getPhase(0), is(phaseGroup.getPhases().get(0)));
    assertThat(phaseGroup.getPhases().size(), is(3));
    assertThat(phaseGroup.getPhases().get(0).getPhaseId(), is(0));
    for (PhaseRef<MutableInt> phaseRef : phaseGroup.getPhases()) {
      assertThat(phaseRef.getPhaseType(), is(PhaseType.Writable));
      assertThat(phaseRef.getLender(), is(intState));
      assertThat(phaseRef.isBorrowed(), is(false));
    }
  }

  @Test
  public void testPhaseByEnum() throws Exception {
    final StateRef<MutableInt> intState = Mock.newStateRepo().newInt();
    final EnumPhaseGroup<MutableInt, OnePass> onePassGroup = intState.phaseBy(OnePass.class);
    final PhaseRef<MutableInt> loadPhase = onePassGroup.getPhase(OnePass.Load);
    assertThat(loadPhase.getPhaseType(), is(PhaseType.Writable));
    assertThat(loadPhase.getPhaseId(), is(0));
    final PhaseRef<MutableInt> updatePhase = onePassGroup.getPhase(OnePass.Update);
    assertThat(updatePhase.getPhaseType(), is(PhaseType.Writable));
    assertThat(updatePhase.getPhaseId(), is(1));
    final PhaseRef<MutableInt> publishPhase = onePassGroup.getPhase(OnePass.Publish);
    assertThat(publishPhase.getPhaseType(), is(PhaseType.ReadOnly));
    assertThat(publishPhase.getPhaseId(), is(2));
  }

  @Test
  public void testPhaseByCustomEnum() throws Exception {
    final StateRef<MutableInt> intState = Mock.newStateRepo().newInt();
    final EnumPhaseGroup<MutableInt, OnePass> customOnePassGroup =
        intState.phaseBy(Lists.immutable.of(
            Tuple.of(OnePass.Load, PhaseType.Broadcast),
            Tuple.of(OnePass.Publish, PhaseType.Writable),
            Tuple.of(OnePass.Update, PhaseType.ReadOnly)));
    final PhaseRef<MutableInt> loadPhase = customOnePassGroup.getPhase(OnePass.Load);
    assertThat(loadPhase.getPhaseType(), is(PhaseType.Broadcast));
    assertThat(loadPhase.getPhaseId(), is(0));
    final PhaseRef<MutableInt> updatePhase = customOnePassGroup.getPhase(OnePass.Update);
    assertThat(updatePhase.getPhaseType(), is(PhaseType.ReadOnly));
    assertThat(updatePhase.getPhaseId(), is(2));
    final PhaseRef<MutableInt> publishPhase = customOnePassGroup.getPhase(OnePass.Publish);
    assertThat(publishPhase.getPhaseType(), is(PhaseType.Writable));
    assertThat(publishPhase.getPhaseId(), is(1));
  }

  @Test
  public void testCopyStateRef() throws Exception {
    final StateRef<MutableInt> originState = Mock.newStateRepo().newInt();
    final StateRef<MutableInt> copyState = originState.copy();
    assertThat(originState.getStateId(), lessThan(copyState.getStateId()));
    assertThat(originState.getFactory(), is(copyState.getFactory()));
    assertThat(originState.getRepo(), is(copyState.getRepo()));
    assertThat(originState.getPartitionCount(), is(copyState.getPartitionCount()));
  }

  @Test
  public void testCopyStateRefWithPartitionCountChanged() throws Exception {
    final StateRef<MutableInt> originState = Mock.newStateRepo().newInt();
    final StateRef<MutableInt> copyState = originState.withPartitionCount(10);
    assertThat(originState.getStateId(), lessThan(copyState.getStateId()));
    assertThat(originState.getFactory(), is(copyState.getFactory()));
    assertThat(originState.getRepo(), is(copyState.getRepo()));
    assertThat(
        originState.getPartitionCount(),
        is(originState.getRepo().getDefaultPartitionCount()));
    assertThat(copyState.getPartitionCount(), is(10));
  }

  @Test
  public void testMultiLevelBorrow() throws Exception {
    final StateRef<MutableInt> firstLevel = Mock.newStateRepo().newInt();
    assertThat(firstLevel.isBorrowed(), is(false));
    final PhaseRef<MutableInt> secondLevel = firstLevel.borrow().newPhase();
    assertThat(firstLevel.isBorrowed(), is(true));
    assertThat(secondLevel.isBorrowed(), is(false));
    final PhaseRef<MutableInt> thirdLevel = secondLevel.borrow().newPhase();
    assertThat(secondLevel.isBorrowed(), is(true));
    assertThat(thirdLevel.isBorrowed(), is(false));
    assertThat(((PhaseRef) thirdLevel.getLender()).getLender(), is(firstLevel));
  }
}
