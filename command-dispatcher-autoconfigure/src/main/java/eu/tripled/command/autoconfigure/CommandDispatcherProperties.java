package eu.tripled.command.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "command-dispatcher")
public class CommandDispatcherProperties {

  private boolean useAsync;

  public boolean isUseAsync() {
    return useAsync;
  }

  public void setUseAsync(boolean useAsync) {
    this.useAsync = useAsync;
  }
}
