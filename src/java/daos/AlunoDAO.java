package daos;
import bd.ConnectionFactory;
import beans.Aluno;
import beans.Contato;
import beans.Escola;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class AlunoDAO {
    
    private final String selectAlunos = "SELECT * FROM aluno ORDER BY nome_aluno";
    private final String selectAlunosAtivos = "SELECT * FROM aluno WHERE status_aluno = 1 ORDER BY nome_aluno";
    private final String selectAlunosFiltro = "SELECT * FROM aluno WHERE nome_aluno LIKE ? ORDER BY nome_aluno";
    
//private final String selectAlunosMarcandoPagamentoMes = "SELECT id_aluno, nome_aluno, telefone_aluno, endereco_aluno, nome_responsavel, cpf_responsavel, id_escola_aluno, periodo_aluno, turma_aluno, horario_casa_ida, horario_escola_ida, horario_escola_volta, horario_casa_volta, valor_mensalidade_aluno, vencimento_aluno, data_inicio_aluno, data_fim_aluno, contrato_aluno, status_aluno, data_nasc_aluno, case when aluno.id_aluno not in (select pagamento.id_aluno from pagamento where pagamento.mes_referencia = ?) THEN 0 else 1 end as pago from aluno where aluno.status_aluno = 1 order by nome_aluno";
    
    private final String selectAlunosMarcandoPagamentoMes = "SELECT * FROM aluno "
            + "                                              CASE "
            + "                                              WHEN aluno.id_aluno "
            + "                                              NOT IN ("
            + "                                              SELECT pagamento.id_aluno FROM pagamento "
            + "                                              WHERE pagamento.mes_referencia = ?) "
            + "                                              THEN 0 ELSE 1 END "
            + "                                              AS pago FROM aluno "
            + "                                              WHERE aluno.status_aluno = 1 "
            + "                                              ORDER by nome_aluno";
    
    private final String selectAluno = "SELECT * FROM aluno WHERE id_aluno = ?";
    private final String selectTotalMensalidades = "	SELECT SUM(valor_mensalidade_aluno) as soma FROM aluno WHERE status_aluno = 1";
    private final String desativarAluno = "UPDATE aluno SET status_aluno = 0, data_fim_aluno = ? WHERE id_aluno = ?";
    private final String ativarAluno = "UPDATE aluno SET status_aluno = 1, data_fim_aluno = ? WHERE id_aluno = ?";
    private final String insertAluno = "INSERT INTO aluno (nome_aluno, telefone_aluno, endereco_aluno,  nome_responsavel,  cpf_responsavel,  id_escola_aluno,  periodo_aluno,  turma_aluno,  horario_casa_ida,  horario_escola_ida,  horario_escola_volta,  horario_casa_volta, valor_mensalidade_aluno,  vencimento_aluno,  data_inicio_aluno, data_nasc_aluno, id_veiculo_aluno) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    //private final String updateAluno = "UPDATE aluno SET nome_aluno=?, telefone_aluno=?, endereco_aluno=?, nome_mae_aluno=?, telefone_mae_aluno=?, nome_pai_aluno=?, telefone_pai_aluno=?, escola_aluno=?, periodo_aluno=?, turma_aluno=?, horario_entrada_aluno=?, horario_saida_aluno=?, valor_mensalidade_aluno=?, vencimento_aluno=?, contrato_aluno=? WHERE id_aluno=?";
    private final String updateAluno = "UPDATE aluno SET nome_aluno = ?, telefone_aluno = ?, endereco_aluno = ?, nome_responsavel = ?, cpf_responsavel = ?, id_escola_aluno = ?, periodo_aluno = ?, turma_aluno = ?, horario_casa_ida = ?, horario_escola_ida = ?, horario_escola_volta = ? , horario_casa_volta = ?, valor_mensalidade_aluno = ?, vencimento_aluno = ?, data_nasc_aluno = ?, id_veiculo_aluno = ? WHERE id_aluno=?";
    private final String[] meses = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
    
    
    public List<Aluno> listarTodos(){
        
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Aluno> lista = new ArrayList();
        try{
            con = ConnectionFactory.getConnection();
            stmt = con.prepareStatement(selectAlunos);
            rs = stmt.executeQuery();
            
            ContatoDAO contato = new ContatoDAO();
            EscolaDAO escola = new EscolaDAO();
            VeiculoDAO veiculo = new VeiculoDAO();
            
            while(rs.next()){
                Aluno aluno = new Aluno(
                        rs.getInt("id_aluno"), 
                        rs.getString("nome_aluno"), 
                        rs.getString("telefone_aluno"), 
                        rs.getString("endereco_aluno"), 
                        rs.getString("data_nasc_aluno"), 
                        rs.getString("nome_responsavel"), 
                        rs.getString("cpf_responsavel"),
                        contato.listarContatos(rs.getInt("id_aluno")),
                        veiculo.listarVeiculo(rs.getInt("id_veiculo_aluno")),
                        escola.listarEscola(rs.getInt("id_escola_aluno")),                          
                        rs.getString("periodo_aluno"), 
                        rs.getString("turma_aluno"), 
                        rs.getString("horario_casa_ida"), 
                        rs.getString("horario_escola_ida"), 
                        rs.getString("horario_escola_volta"), 
                        rs.getString("horario_casa_volta"), 
                        rs.getFloat("valor_mensalidade_aluno"), 
                        rs.getInt("vencimento_aluno"), 
                        rs.getString("data_inicio_aluno"), 
                        rs.getString("data_fim_aluno"), 
                        rs.getInt("status_aluno")
                         
                );
               
                lista.add(aluno);
            }
            return lista;
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao listar alunos. "+ex.getMessage());
        }finally{
            if(rs != null){try{rs.close();}catch(SQLException ex){System.out.println("Erro ao fechar Result Set. Ex="+ex.getMessage());}}
            if(stmt != null){try{stmt.close();}catch(SQLException ex){System.out.println("Erro ao fechar o Prepared Statement. Ex="+ex.getMessage());}}
            if(con != null){try{con.close();}catch(SQLException ex){System.out.println("Erro ao fechar a Conexão. Ex="+ex.getMessage());}}
        }
    }
    
    public List<Aluno> listarAlunosAtivos(){
        
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Aluno> lista = new ArrayList();
        try{
            con = ConnectionFactory.getConnection();
            stmt = con.prepareStatement(selectAlunosAtivos);
            rs = stmt.executeQuery();
            
            ContatoDAO contato = new ContatoDAO();
            EscolaDAO escola = new EscolaDAO();
            VeiculoDAO veiculo = new VeiculoDAO();
            
            while(rs.next()){
                Aluno aluno = new Aluno(
                        rs.getInt("id_aluno"), 
                        rs.getString("nome_aluno"), 
                        rs.getString("telefone_aluno"), 
                        rs.getString("endereco_aluno"), 
                        rs.getString("data_nasc_aluno"), 
                        rs.getString("nome_responsavel"), 
                        rs.getString("cpf_responsavel"),
                        contato.listarContatos(rs.getInt("id_aluno")),
                        veiculo.listarVeiculo(rs.getInt("id_veiculo_aluno")),
                        escola.listarEscola(rs.getInt("id_escola_aluno")),                          
                        rs.getString("periodo_aluno"), 
                        rs.getString("turma_aluno"), 
                        rs.getString("horario_casa_ida"), 
                        rs.getString("horario_escola_ida"), 
                        rs.getString("horario_escola_volta"), 
                        rs.getString("horario_casa_volta"), 
                        rs.getFloat("valor_mensalidade_aluno"), 
                        rs.getInt("vencimento_aluno"), 
                        rs.getString("data_inicio_aluno"), 
                        rs.getString("data_fim_aluno"), 
                        rs.getInt("status_aluno")
                );
               
                lista.add(aluno);
            }
            return lista;
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao listar alunos. "+ex.getMessage());
        }finally{
            if(rs != null){try{rs.close();}catch(SQLException ex){System.out.println("Erro ao fechar Result Set. Ex="+ex.getMessage());}}
            if(stmt != null){try{stmt.close();}catch(SQLException ex){System.out.println("Erro ao fechar o Prepared Statement. Ex="+ex.getMessage());}}
            if(con != null){try{con.close();}catch(SQLException ex){System.out.println("Erro ao fechar a Conexão. Ex="+ex.getMessage());}}
        }
    }
    
    public List<Aluno> listarAlunosMarcandoPagamentosMes(){
        
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Aluno> lista = new ArrayList();
        try{
            
            DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd"); 
            String data = fmt.format(new Date());
            int mes = Integer.valueOf(data.substring(5, 7));
            
            con = ConnectionFactory.getConnection();
            stmt = con.prepareStatement(selectAlunosMarcandoPagamentoMes);
            stmt.setString(1, meses[mes-1] + "/" + data.substring(0, 4));
            
            rs = stmt.executeQuery();
            
            ContatoDAO contato = new ContatoDAO();
            EscolaDAO escola = new EscolaDAO();
            VeiculoDAO veiculo = new VeiculoDAO();
            
            while(rs.next()){
                
                Aluno aluno = new Aluno(
                        rs.getInt("id_aluno"), 
                        rs.getString("nome_aluno"), 
                        rs.getString("telefone_aluno"), 
                        rs.getString("endereco_aluno"), 
                        rs.getString("data_nasc_aluno"), 
                        rs.getString("nome_responsavel"), 
                        rs.getString("cpf_responsavel"),
                        contato.listarContatos(rs.getInt("id_aluno")),
                        veiculo.listarVeiculo(rs.getInt("id_veiculo_aluno")),
                        escola.listarEscola(rs.getInt("id_escola_aluno")),                          
                        rs.getString("periodo_aluno"), 
                        rs.getString("turma_aluno"), 
                        rs.getString("horario_casa_ida"), 
                        rs.getString("horario_escola_ida"), 
                        rs.getString("horario_escola_volta"), 
                        rs.getString("horario_casa_volta"), 
                        rs.getFloat("valor_mensalidade_aluno"), 
                        rs.getInt("vencimento_aluno"), 
                        rs.getString("data_inicio_aluno"), 
                        rs.getString("data_fim_aluno"), 
                        rs.getInt("status_aluno")
                );
                
                lista.add(aluno);
            }
            return lista;
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao listar alunos. "+ex.getMessage());
        }finally{
            if(rs != null){try{rs.close();}catch(SQLException ex){System.out.println("Erro ao fechar Result Set. Ex="+ex.getMessage());}}
            if(stmt != null){try{stmt.close();}catch(SQLException ex){System.out.println("Erro ao fechar o Prepared Statement. Ex="+ex.getMessage());}}
            if(con != null){try{con.close();}catch(SQLException ex){System.out.println("Erro ao fechar a Conexão. Ex="+ex.getMessage());}}
        }
    }
    
    public List<Aluno> listarAlunos(String filtro) {
        
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Aluno> lista = new ArrayList();
        try{
            con = ConnectionFactory.getConnection();
            stmt = con.prepareStatement(selectAlunosFiltro);
            filtro = "%" + filtro + "%";
            stmt.setString(1, filtro);
            rs = stmt.executeQuery();
            
            ContatoDAO contato = new ContatoDAO();
            EscolaDAO escola = new EscolaDAO();
            VeiculoDAO veiculo = new VeiculoDAO();
            
            while(rs.next()){
                Aluno aluno = new Aluno(
                        rs.getInt("id_aluno"), 
                        rs.getString("nome_aluno"), 
                        rs.getString("telefone_aluno"), 
                        rs.getString("endereco_aluno"), 
                        rs.getString("data_nasc_aluno"), 
                        rs.getString("nome_responsavel"), 
                        rs.getString("cpf_responsavel"),
                        contato.listarContatos(rs.getInt("id_aluno")),
                        veiculo.listarVeiculo(rs.getInt("id_veiculo_aluno")),
                        escola.listarEscola(rs.getInt("id_escola_aluno")),                          
                        rs.getString("periodo_aluno"), 
                        rs.getString("turma_aluno"), 
                        rs.getString("horario_casa_ida"), 
                        rs.getString("horario_escola_ida"), 
                        rs.getString("horario_escola_volta"), 
                        rs.getString("horario_casa_volta"), 
                        rs.getFloat("valor_mensalidade_aluno"), 
                        rs.getInt("vencimento_aluno"), 
                        rs.getString("data_inicio_aluno"), 
                        rs.getString("data_fim_aluno"), 
                        rs.getInt("status_aluno")
                );
               
                lista.add(aluno);
            }
            return lista;
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao listar alunos. "+ex.getMessage());
        }finally{
            if(rs != null){try{rs.close();}catch(SQLException ex){System.out.println("Erro ao fechar Result Set. Ex="+ex.getMessage());}}
            if(stmt != null){try{stmt.close();}catch(SQLException ex){System.out.println("Erro ao fechar o Prepared Statement. Ex="+ex.getMessage());}}
            if(con != null){try{con.close();}catch(SQLException ex){System.out.println("Erro ao fechar a Conexão. Ex="+ex.getMessage());}}
        }
    }
    
    public Aluno listarAluno(int id) {
        
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try{
            
            con = ConnectionFactory.getConnection();
            stmt = con.prepareStatement(selectAluno);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            
            ContatoDAO contato = new ContatoDAO();
            EscolaDAO escola = new EscolaDAO();
            VeiculoDAO veiculo = new VeiculoDAO();
            
            Aluno aluno = null;
            
            while(rs.next()){
                
                aluno = new Aluno(
                        rs.getInt("id_aluno"), 
                        rs.getString("nome_aluno"), 
                        rs.getString("telefone_aluno"), 
                        rs.getString("endereco_aluno"), 
                        rs.getString("data_nasc_aluno"), 
                        rs.getString("nome_responsavel"), 
                        rs.getString("cpf_responsavel"),
                        contato.listarContatos(rs.getInt("id_aluno")),
                        veiculo.listarVeiculo(rs.getInt("id_veiculo_aluno")),
                        escola.listarEscola(rs.getInt("id_escola_aluno")),                          
                        rs.getString("periodo_aluno"), 
                        rs.getString("turma_aluno"), 
                        rs.getString("horario_casa_ida"), 
                        rs.getString("horario_escola_ida"), 
                        rs.getString("horario_escola_volta"), 
                        rs.getString("horario_casa_volta"), 
                        rs.getFloat("valor_mensalidade_aluno"), 
                        rs.getInt("vencimento_aluno"), 
                        rs.getString("data_inicio_aluno"), 
                        rs.getString("data_fim_aluno"), 
                        rs.getInt("status_aluno")
                );
            }
            return aluno;
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao listar o aluno. "+ex.getMessage());
        }finally{
            if(rs != null){try{rs.close();}catch(SQLException ex){System.out.println("Erro ao fechar Result Set. Ex="+ex.getMessage());}}
            if(stmt != null){try{stmt.close();}catch(SQLException ex){System.out.println("Erro ao fechar o Prepared Statement. Ex="+ex.getMessage());}}
            if(con != null){try{con.close();}catch(SQLException ex){System.out.println("Erro ao fechar a Conexão. Ex="+ex.getMessage());}}
        }
    }
    
    public void desativarAluno(int id) {
        Connection con = null;
        PreparedStatement stmt = null;
        try{
            con = ConnectionFactory.getConnection();
            stmt = con.prepareStatement(desativarAluno);
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd"); 
            String data = fmt.format(new Date()); 
            stmt.setString(1, data);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } 
        catch (SQLException ex) {
            throw new RuntimeException("Erro ao deletar aluno. "+ex.getMessage());
        }
        finally{
            if(stmt != null){try{stmt.close();}catch(SQLException ex){System.out.println("Erro ao fechar o Prepared Statement. Ex="+ex.getMessage());}}
            if(con != null){try{con.close();}catch(SQLException ex){System.out.println("Erro ao fechar a Conexão. Ex="+ex.getMessage());}}
        }
    }
    
    public void ativarAluno(int id) {
        Connection con = null;
        PreparedStatement stmt = null;
        try{
            con = ConnectionFactory.getConnection();
            stmt = con.prepareStatement(ativarAluno); 
            stmt.setString(1, "");
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } 
        catch (SQLException ex) {
            throw new RuntimeException("Erro ao deletar aluno. "+ex.getMessage());
        }
        finally{
            if(stmt != null){try{stmt.close();}catch(SQLException ex){System.out.println("Erro ao fechar o Prepared Statement. Ex="+ex.getMessage());}}
            if(con != null){try{con.close();}catch(SQLException ex){System.out.println("Erro ao fechar a Conexão. Ex="+ex.getMessage());}}
        }
    }
    
    
    public Aluno cadastraAluno(Aluno a)
    {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            con = ConnectionFactory.getConnection();
            stmt = con.prepareStatement(insertAluno, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, a.getNome());
            stmt.setString(2, a.getTelefone());
            stmt.setString(3, a.getEndereco());
            stmt.setString(4, a.getNomeResponsavel());
            stmt.setString(5, a.getCpfResponsavel());
            stmt.setInt(6, a.getEscola().getId());            
            stmt.setString(7, a.getPeriodo());
            stmt.setString(8, a.getTurma());
            stmt.setString(9, a.getHorarioCasaIda());
            stmt.setString(10, a.getHorarioEscolaIda());
            stmt.setString(11, a.getHorarioEscolaVolta());
            stmt.setString(12, a.getHorarioCasaVolta());
            stmt.setFloat(13, a.getMensalidade());
            stmt.setInt(14, a.getVencimento());
            stmt.setString(15, a.getDataInicio());
            stmt.setString(16, a.getDataNascimento());
            stmt.setInt(17, a.getVeiculo().getId());
            stmt.executeUpdate();
            rs = stmt.getGeneratedKeys();
            rs.next();
            int id = rs.getInt(1);
            a.setId(id);
            ContatoDAO contato = new ContatoDAO();
            for(Contato c : a.getContatos()){
               contato.registrarContato(c, id);
            }
            return a;
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao inserir um aluno. "+ex.getMessage());
        } finally{
            if(rs != null){try{rs.close();}catch(SQLException ex){System.out.println("Erro ao fechar Result Set. Ex="+ex.getMessage());}}
            if(stmt != null){try{stmt.close();}catch(SQLException ex){System.out.println("Erro ao fechar o Prepared Statement. Ex="+ex.getMessage());}}
            if(con != null){try{con.close();}catch(SQLException ex){System.out.println("Erro ao fechar a Conexão. Ex="+ex.getMessage());}}
        }
    }
    
    
    public void updateAluno(Aluno a)
    {
        Connection con = null;
        PreparedStatement stmt = null;
        try{
            con = ConnectionFactory.getConnection();
            stmt = con.prepareStatement(updateAluno);
            stmt.setString(1, a.getNome());
            stmt.setString(2, a.getTelefone());
            stmt.setString(3, a.getEndereco());
            stmt.setString(4, a.getNomeResponsavel());
            stmt.setString(5, a.getCpfResponsavel());
            stmt.setInt(6, a.getEscola().getId());            
            stmt.setString(7, a.getPeriodo());
            stmt.setString(8, a.getTurma());
            stmt.setString(9, a.getHorarioCasaIda());
            stmt.setString(10, a.getHorarioEscolaIda());
            stmt.setString(11, a.getHorarioEscolaVolta());
            stmt.setString(12, a.getHorarioCasaVolta());
            stmt.setFloat(13, a.getMensalidade());
            stmt.setInt(14, a.getVencimento());
            stmt.setString(15, a.getDataNascimento());
            stmt.setInt(16, a.getVeiculo().getId());
            stmt.setInt(17, a.getId());
            stmt.executeUpdate();
            
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao alterar um aluno no banco de dados. "+ex.getMessage());
        } finally{
            if(stmt != null){try{stmt.close();}catch(SQLException ex){System.out.println("Erro ao fechar o Prepared Statement. Ex="+ex.getMessage());}}
            if(con != null){try{con.close();}catch(SQLException ex){System.out.println("Erro ao fechar a Conexão. Ex="+ex.getMessage());}}
        }
    }
    
    public double totalMensalidades(){
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            con = ConnectionFactory.getConnection();
            stmt = con.prepareStatement(selectTotalMensalidades);
            rs = stmt.executeQuery();
            double total = 0.0;
            while(rs.next()){
                total = rs.getDouble("soma");
            }
            return total;
        } catch (SQLException ex) {
            throw new RuntimeException("Erro ao listar o aluno. "+ex.getMessage());
        }finally{
            if(rs != null){try{rs.close();}catch(SQLException ex){System.out.println("Erro ao fechar Result Set. Ex="+ex.getMessage());}}
            if(stmt != null){try{stmt.close();}catch(SQLException ex){System.out.println("Erro ao fechar o Prepared Statement. Ex="+ex.getMessage());}}
            if(con != null){try{con.close();}catch(SQLException ex){System.out.println("Erro ao fechar a Conexão. Ex="+ex.getMessage());}}
        }
    }

}