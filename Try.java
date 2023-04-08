/**
 * CS2030S PE2 Question 1
 * AY20/21 Semester 2
 *
 * @author A0255516A
 */

package cs2030s.fp;

public abstract class Try<T> {
  public abstract T get() throws Throwable;

  public abstract <U> Try<U> map(Transformer<? super T, ? extends U> trans);

  public abstract <U> Try<U> flatMap(Transformer<? super T, ? extends Try<? extends U>> trans);

  public abstract Try<T> onFailure(Consumer<? super Throwable> cons);

  public abstract Try<T> recover(Transformer<? super Throwable, ? extends T> trans);

  public static <T> Try<T> of(Producer<? extends T> prod) {
    try {
      return Try.success(prod.produce());
    } catch (Throwable e) {
      return Try.failure(e);
    }
  } 

  public static <T> Try<T> success(T t) {
    return new Success<>(t);
  }

  public static <T> Try<T> failure(Throwable throwable) { 
    return new Failure<>(throwable);
  }

  private static class Success<T> extends Try<T> {
    private T value;

    private Success(T t) {
      this.value = t;
    }

    public T get() {
      return this.value;
    }

    @Override
    public <U> Try<U> map(Transformer<? super T, ? extends U> trans) {
      return Try.of(() -> trans.transform(this.get()));
    }

    @Override
    public <U> Try<U> flatMap(Transformer<? super T, ? extends Try<? extends U>> trans) {
      @SuppressWarnings("unchecked") 
      Try<U> temp = (Try<U>) trans.transform(this.get());
      return temp;
    }

    @Override
    public Try<T> onFailure(Consumer<? super Throwable> cons) {
      return this;
    }

    @Override
    public Try<T> recover(Transformer<? super Throwable, ? extends T> trans) {
      return this;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof Success<?>) {
        Success<?> temp = (Success<?>) obj;
        if (this.value == null) {
          return temp.value == null;
        } else {
          return this.value.equals(temp.get());
        }
      }
      return false;
    }
  }

  private static class Failure<T> extends Try<T> {
    private Throwable throwable;

    private Failure(Throwable throwable) {
      this.throwable = throwable;
    }

    public T get() throws Throwable {
      throw this.throwable;
    }

    @Override
    public <U> Try<U> map(Transformer<? super T, ? extends U> trans) {
      @SuppressWarnings("unchecked") 
      Try<U> temp = (Try<U>) this;
      return temp;
    }

    @Override
    public <U> Try<U> flatMap(Transformer<? super T, ? extends Try<? extends U>> trans) {
      @SuppressWarnings("unchecked") 
      Try<U> temp = (Try<U>) this;
      return temp;
    }

    @Override
    public Try<T> onFailure(Consumer<? super Throwable> cons) {
      try {
        cons.consume(this.throwable);
        return this;
      } catch (Throwable e) {
        return Try.failure(e);
      }
    }

    @Override
    public Try<T> recover(Transformer<? super Throwable, ? extends T> trans) {
      return Try.of(() -> trans.transform(this.throwable));
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof Failure<?>) {
        Failure<?> temp = (Failure<?>) obj;
        return String.valueOf(temp.throwable) ==
          String.valueOf(this.throwable);
      }
      return false;
    }
  }

}
