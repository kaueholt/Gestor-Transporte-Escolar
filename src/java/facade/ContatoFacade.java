/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facade;

import beans.Contato;
import daos.ContatoDAO;
import java.util.List;

/**
 *
 * @author julio
 */
public class ContatoFacade {
    
    public static Contato inserir(Contato a, int id_aluno){
        ContatoDAO dao = new ContatoDAO();
        return dao.registrarContato(a, id_aluno);
    }
    public static List<Contato> listar(int id_aluno){
        ContatoDAO dao = new ContatoDAO();
        return dao.listarContatos(id_aluno);
    }
    public static void deletar(int id){
        ContatoDAO dao = new ContatoDAO();
        dao.excluirContato(id);
    }
}
