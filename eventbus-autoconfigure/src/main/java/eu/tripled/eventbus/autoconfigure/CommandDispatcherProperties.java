package eu.tripled.eventbus.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "eu.tripled.eventbus")
public class CommandDispatcherProperties {

  private boolean useAsync;

  public boolean isUseAsync() {
    return useAsync;
  }

  public void setUseAsync(boolean useAsync) {
    this.useAsync = useAsync;
  }
}
