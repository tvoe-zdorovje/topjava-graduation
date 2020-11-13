package ru.javawebinar.topjava;

import javax.validation.groups.Default;

public class View {
    public interface Statistic extends Regular {}
    public interface Regular {}

    public interface ValidatedUI extends Default {}
}
