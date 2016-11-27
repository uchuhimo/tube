package uchuhimo.tube.value.tuple;

import org.immutables.value.Value;

@Value.Style(
    allParameters = true,
    typeImmutable = "*Impl",
    defaults = @Value.Immutable(builder = false))
public @interface TupleStyle {
}
