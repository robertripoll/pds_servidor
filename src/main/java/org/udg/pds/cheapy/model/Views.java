package org.udg.pds.cheapy.model;

public class Views
{
    public static class Basic
    {
    }

    public static class Summary extends Basic
    {
    }

    public static class Public extends Summary
    {
    }

    public static class Interactor extends Public
    {
    }

    public static class Private extends Interactor
    {
    }
}