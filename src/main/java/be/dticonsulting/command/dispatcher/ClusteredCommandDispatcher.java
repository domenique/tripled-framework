package be.dticonsulting.command.dispatcher;

import be.dticonsulting.command.Command;
import be.dticonsulting.command.CommandCallback;
import be.dticonsulting.command.CommandDispatcher;

/**
 * A CommandDispatcher implementation which uses middleware to dispatch commands to worker instances capable of
 * executing commands.
 * This implementation will use an AMQP broker to dispatch events.
 */
public class ClusteredCommandDispatcher implements CommandDispatcher {

	@Override
	public <ReturnType> void dispatch(Command<ReturnType> command, CommandCallback<ReturnType> callback) {

	}

	@Override
	public <ReturnType> void dispatch(Command<ReturnType> command) {

	}
}
