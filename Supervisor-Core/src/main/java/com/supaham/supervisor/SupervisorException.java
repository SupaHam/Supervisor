package com.supaham.supervisor;

public class SupervisorException extends Exception {

  public SupervisorException(String message) {
    super(message);
  }

  public SupervisorException(String message, Throwable cause) {
    super(message, cause);
  }

  public SupervisorException(Throwable cause) {
    super(cause);
  }
}
