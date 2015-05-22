package be.dticonsulting.command;

import static org.assertj.core.api.Assertions.*;

import be.dticonsulting.command.statistic.CommandDispatcherStatistic;
import org.junit.Test;

public class CommandDispatcherStatisticTest {

  @Test
  public void whenGivenNothing_shouldBeEmpty() throws Exception {
    // given

    // when
    CommandDispatcherStatistic<MyCommand> statistic = new CommandDispatcherStatistic<>();

    // then
    assertThat(statistic.getAverage())
        .isEqualTo(0.0F);
    assertThat(statistic.getExecutionCount())
        .isEqualTo(0);
  }

  @Test
  public void whenGivenZero_shouldBeEmpty() throws Exception {
    // given
    CommandDispatcherStatistic<MyCommand> statistic = new CommandDispatcherStatistic<>();

    // when
    statistic.addExecution(0);

    // then
    assertThat(statistic.getAverage())
        .isEqualTo(0.0F);
    assertThat(statistic.getExecutionCount())
        .isEqualTo(1);
  }

  @Test
  public void whenGiven1Execution_averageShouldBeThatNumber() throws Exception {
    // given
    CommandDispatcherStatistic<MyCommand> statistic = new CommandDispatcherStatistic<>();

    // when
    statistic.addExecution(12);

    // then
    assertThat(statistic.getAverage())
        .isEqualTo(12.0F);
    assertThat(statistic.getExecutionCount())
        .isEqualTo(1);
  }

  @Test
  public void whenGiven2Executions_averageShouldCalculated() throws Exception {
    // given
    CommandDispatcherStatistic<MyCommand> statistic = new CommandDispatcherStatistic<>();

    // when
    statistic.addExecution(6);
    statistic.addExecution(11);

    // then
    assertThat(statistic.getAverage())
        .isEqualTo(8.5F);
    assertThat(statistic.getExecutionCount())
        .isEqualTo(2);
  }

  @Test
  public void whenGiven3Executions_averageShouldCalculated() throws Exception {
    // given
    CommandDispatcherStatistic<MyCommand> statistic = new CommandDispatcherStatistic<>();

    // when
    statistic.addExecution(6);
    statistic.addExecution(11);
    statistic.addExecution(7);

    // then
    assertThat(statistic.getAverage())
        .isEqualTo(8.0F);
    assertThat(statistic.getExecutionCount())
        .isEqualTo(3);
  }

  private class MyCommand implements Command<Void> {

    @Override
    public Void execute() {
      return null;
    }
  }
}