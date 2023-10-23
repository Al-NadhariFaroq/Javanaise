package jvn.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@code JvnLockMethod} annotation is used within the Javanaise framework to specify the locking behavior of a
 * method in a shared Java class.
 *
 * <p>The {@code JvnLockMethod} annotation has the following attributes:
 * <ul>
 *   <li>{@code	lockType}: Specifies the type of locking to be used for the annotated method. This attribute can only
 *       take one of two values: {@code JvnLockType.READ} or {@code JvnLockType.WRITE}.
 *   </li>
 * </ul>
 *
 * <p>Example usage:
 * <pre>{@code
 *   @JvnLockMethod(lockType = JvnLockType.READ)
 *   public void performReadOperation() {
 *       // Method implementation
 *   }
 * }</pre>
 *
 * <p>In this example, the {@code JvnLockMethod} annotation is applied to a method with the specified {@code lockType}
 * of {@code JvnLockType.READ}, indicating that the method should be executed under a read lock when accessed
 * concurrently in a distributed Javanaise environment.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface JvnLockMethod {
	/**
	 * Specifies the type of locking to be used for the annotated method.
	 *
	 * @return The lock type for the method, which can only be {@code JvnLockType.READ} or {@code JvnLockType.WRITE}.
	 */
	JvnLockType lockType();
}

