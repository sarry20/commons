package team.unnamed.error;

import team.unnamed.validate.Validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;

/**
 * Contains a collection of {@link Throwable} messages
 * thrown during a process. This details are mutable,
 * you can add more {@link Throwable} messages and
 * you can merge this error details with another
 * ErrorDetails. Util for collect all thrown errors
 * and not only print the first.
 */
public final class ErrorDetails {

	private final String heading; // the first displayed message
	private final List<String> messages;

	/**
	 * Constructs the error details with initial messages
	 * and a heading.
	 * @param heading  The error details heading
	 * @param messages The list of error messages
	 */
	public ErrorDetails(String heading, Collection<String> messages) {
		Validate.isNotNull(messages, "messages");
		this.heading = Validate.isNotNull(heading, "heading");
		this.messages = new ArrayList<>(messages);
	}

	/**
	 * Constructs the error details using only the
	 * heading. Creates an empty list for the error messages.
	 * @param heading The heading
	 */
	public ErrorDetails(String heading) {
		this.heading = Validate.isNotNull(heading, "heading");
		this.messages = new ArrayList<>();
	}

	/**
	 * Adds all the error messages that the parameter
	 * "details" holds, returns an ErrorDetails object
	 * that contains all the messages (this.messages and
	 * details.messages), also uses this.heading and
	 * ignores details.heading.
	 * @param details The details
	 * @return The merged details
	 */
	public synchronized ErrorDetails merge(ErrorDetails details) {
		Validate.isNotNull(details, "details");
		this.messages.addAll(details.messages);
		return this;
	}

	/**
	 * Gets the error stack-trace using {@link Errors#getStackTrace}
	 * and executes {@link ErrorDetails#add(String)} to add
	 * the stack-trace to the list of messages.
	 * @param error The error
	 * @return This error details, for a fluent-api.
	 */
	public synchronized ErrorDetails add(Throwable error) {
		return add(Errors.getStackTrace(error));
	}

	/**
	 * Adds the specified message to the list of
	 * messages.
	 * @param message The message, not null and
	 *                not empty.
	 * @return This error details, for a fluent-api
	 */
	public synchronized ErrorDetails add(String message) {
		this.messages.add(Validate.isNotEmpty(message));
		return this;
	}

	/**
	 * @return The error messages count.
	 */
	public synchronized int errorCount() {
		return this.messages.size();
	}

	/**
	 * Formats the error messages. The messages are joined
	 * into "\n" and enumerated. The heading is appended first.
	 * @return The formatted error messages, everything enumerated.
	 */
	public synchronized String format() {

		StringJoiner joiner = new StringJoiner("\n");
		joiner.add(heading);

		for (int i = 0; i < this.messages.size(); i++) {
			int number = i + 1;
			String message = this.messages.get(i);
			joiner.add(number + ") " + message);
		}

		return joiner.toString();
	}

	/**
	 * Constructs an exception using the current exceptions messages.
	 * @param constructor The constructor function. For example, creating
	 *                    a IllegalStateException: toException(IllegalStateException::new)
	 * @param <T>         The type of the exception
	 * @return The created exception
	 */
	public synchronized <T extends Exception> T toException(Function<String, T> constructor) {
		return constructor.apply(format());
	}

}
