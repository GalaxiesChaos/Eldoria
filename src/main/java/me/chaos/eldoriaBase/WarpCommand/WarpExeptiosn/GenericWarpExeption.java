package me.chaos.eldoriaBase.WarpCommand.WarpExeptiosn;

public class GenericWarpExeption extends Exception{
    public GenericWarpExeption(String msg){
        super("[WarpError]" + msg);
    }
}
