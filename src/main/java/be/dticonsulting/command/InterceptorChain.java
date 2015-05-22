package be.dticonsulting.command;

public interface InterceptorChain<ReturnType> {

	ReturnType proceed() throws Throwable;
}
