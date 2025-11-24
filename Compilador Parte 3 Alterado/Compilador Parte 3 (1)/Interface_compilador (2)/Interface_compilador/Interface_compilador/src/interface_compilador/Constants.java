package interface_compilador;



public interface Constants extends ScannerConstants, ParserConstants
{
    int EPSILON  = 0;
    int DOLLAR   = 1;

    int t_id = 2;
    int t_TOKEN_3 = 3; //"+"
    int t_Const_int = 4;
    int t_Const_float = 5;
    int t_Const_string = 6;
    int t_Comentario_linha = 7;
    int t_Comentario_bloco = 8;
    int t_pr_tipoInt = 9;
    int t_pr_tipoFloat = 10;
    int t_pr_tipoString = 11;
    int t_pr_tipoBoolean = 12;
    int t_pr_list = 13;
    int t_pr_add = 14;
    int t_pr_delete = 15;
    int t_pr_read = 16;
    int t_pr_print = 17;
    int t_pr_if = 18;
    int t_pr_else = 19;
    int t_pr_end = 20;
    int t_pr_do = 21;
    int t_pr_until = 22;
    int t_pr_begin = 23;
    int t_pr_and = 24;
    int t_pr_or = 25;
    int t_pr_not = 26;
    int t_pr_count = 27;
    int t_pr_size = 28;
    int t_pr_elementOf = 29;
    int t_pr_true = 30;
    int t_pr_false = 31;
    int t_TOKEN_32 = 32; //"-"
    int t_TOKEN_33 = 33; //"*"
    int t_TOKEN_34 = 34; //"/"
    int t_TOKEN_35 = 35; //"=="
    int t_TOKEN_36 = 36; //"~="
    int t_TOKEN_37 = 37; //"<"
    int t_TOKEN_38 = 38; //">"
    int t_TOKEN_39 = 39; //"="
    int t_TOKEN_40 = 40; //"<-"
    int t_TOKEN_41 = 41; //"("
    int t_TOKEN_42 = 42; //")"
    int t_TOKEN_43 = 43; //";"
    int t_TOKEN_44 = 44; //","
}

