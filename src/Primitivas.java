public abstract class Primitivas {
    public int aritimeticoAdd(int acc, int op1)
    {
        return acc + op1;
    }
    public int aritimeticoSub(int acc, int op1)
    {
        return acc - op1;
    }
    public int aritimeticoMult(int acc, int op1)
    {
        return acc * op1;
    }
    public int aritimeticoDiv(int acc, int op1)
    {
        return acc / op1;
    }
    public int memoriaLoad(int op1)
    {
        return op1;
    }
    public String memoriaStore(int acc)
    {
        return String.valueOf(acc);
    }
    public int saltoBRANY(int labelAddress)
    {
        return labelAddress;
    }
    public int saltoBRPOS(int pc, int labelAddress, int acc)
    {
        if (acc > 0) return labelAddress;
        else return ++pc;
    }
    public int saltoBRZERO(int pc, int labelAddress, int acc)
    {
        if (acc == 0) return labelAddress;
        else return ++pc;
    }
    public int saltoBRNEG(int pc, int labelAddress, int acc)
    {
        if (acc < 0) return labelAddress;
        else return ++pc;
    }
    public abstract EstadoProcesso sistemaSYSCALL(String index);
}
