package uchuhimo.tube.state;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import uchuhimo.tube.state.util.Mock;
import uchuhimo.tube.value.primitive.MutableInt;

import org.junit.Test;

public class PhaseRepoTest {
  @Test
  public void testNewPhaseFromRepo() throws Exception {
    final StateRef<MutableInt> intState = Mock.newStateRepo().newInt();
    assertThat(intState.isBorrowed(), is(false));
    final PhaseRepo<MutableInt> phaseRepo = intState.borrow();
    assertThat(intState.isBorrowed(), is(true));
    assertThat(phaseRepo.getLender(), is(intState));
    final PhaseRef<MutableInt> defaultPhase = phaseRepo.newPhase();
    assertThat(defaultPhase.getPhaseType(), is(PhaseType.Writable));
    assertThat(defaultPhase.getPhaseId(), is(0));
    assertThat(defaultPhase.getLender(), is(intState));
    final PhaseRef<MutableInt> broadcastPhase = phaseRepo.newBroadcast();
    assertThat(broadcastPhase.getPhaseType(), is(PhaseType.Broadcast));
    assertThat(broadcastPhase.getPhaseId(), is(1));
    final PhaseRef<MutableInt> readOnlyPhase = phaseRepo.newReadOnly();
    assertThat(readOnlyPhase.getPhaseType(), is(PhaseType.ReadOnly));
    assertThat(readOnlyPhase.getPhaseId(), is(2));
    final PhaseRef<MutableInt> writablePhase = phaseRepo.newWritable();
    assertThat(writablePhase.getPhaseType(), is(PhaseType.Writable));
    assertThat(writablePhase.getPhaseId(), is(3));
  }
}
