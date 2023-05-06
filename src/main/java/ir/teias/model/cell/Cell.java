package ir.teias.model.cell;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class Cell<T> {
    protected final T value;
    protected final CellType cellType;
}
