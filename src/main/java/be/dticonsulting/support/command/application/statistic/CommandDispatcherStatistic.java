package be.dticonsulting.support.command.application.statistic;

import be.dticonsulting.support.command.application.Command;

public class CommandDispatcherStatistic<T extends Command> {

  private Long executionCount;
  private Float averageExecutionTime;

  public CommandDispatcherStatistic() {
    executionCount = 0L;
    averageExecutionTime = 0.0F;
  }

  public void addExecution(long milliseconds) {
    float sumOfTimings = (executionCount * averageExecutionTime) + milliseconds;
    averageExecutionTime = sumOfTimings / ++executionCount;
  }

  public Float getAverage() {
    return averageExecutionTime;
  }

  public Long getExecutionCount() {
    return executionCount;
  }

}
