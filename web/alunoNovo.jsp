<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@page import="java.util.Locale"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.List"%>
<%@page import="beans.Aluno"%>
<%@page import="beans.LoginBean"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<c:set var="login" value="${sessionScope.usuario}" />
<c:set var="marcador" value="1" />
<c:if test="${login == null}">
   <jsp:forward page="/index.jsp">
        <jsp:param name="msg" value="Usuário deve se autenticar para acessar o sistema." />
        <jsp:param name="red" value="Alunos" />
    </jsp:forward> 
</c:if>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" href="http://code.jquery.com/ui/1.9.0/themes/base/jquery-ui.css" />
        <script src="http://code.jquery.com/jquery-1.8.2.js"></script>
        <script src="http://code.jquery.com/ui/1.9.0/jquery-ui.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery.mask/1.14.11/jquery.mask.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-maskmoney/3.0.2/jquery.maskMoney.min.js" type="text/javascript"></script>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <script>
            $(function() {
                $("#calendario").datepicker({dateFormat: 'dd/mm/yy'});
            });
        </script>
        <title>Novo Aluno</title>
    </head>
    <body>
        <%@ include file="nav.jsp" %>
        
        <div class="container" style="padding-top: 1%">
            <div class="jumbotron">
            
                <form class="form-horizontal" action="Alunos?action=novo" method="POST">
                    <fieldset>
                        <legend>Aluno</legend>
                        
                        <div class="form-group">
                          <label class="control-label col-sm-2" for="nome">Nome:</label>
                          <div class="col-sm-10">
                              <input type="text" class="form-control" name="nome" required>
                          </div>
                        </div>
                        <div class="form-group">
                          <label class="control-label col-sm-2" for="telefone">Telefone:</label>
                          <div class="col-sm-10">
                              <input type="text" class="form-control telefone" name="telefone" maxlength="15" required>
                          </div>
                        </div>
                        <div class="form-group">
                          <label class="control-label col-sm-2" for="endereco">Endereço:</label>
                          <div class="col-sm-10">
                              <input type="text" class="form-control" name="endereco" maxlength="100" required>
                          </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-sm-2" for="data">Data de Nascimento:</label>
                            <div class="col-sm-10">
                                <input type="date" name="data" class="form-control" required "pattern="dd/MM/yyyy" />
                            </div>
                          </div>
                    
                    </fieldset>
                    <fieldset>
                        <legend>Contatos</legend>
                        
                        <div class="form-group">
                          <label class="control-label col-sm-2" for="nome_contato">Nome:</label>
                          <div class="col-sm-7">
                              <input type="text" class="form-control" id="nome_contato">
                          </div>
                        </div>
                        <div class="form-group">
                          <label class="control-label col-sm-2" for="telefone_contato">Telefone:</label>
                          <div class="col-sm-7">
                              <input type="text" class="form-control telefone" id="telefone_contato" maxlength="15">
                          </div>
                        </div>
                        <div class="form-group">
                          <label class="control-label col-sm-2" for="parentesco">Parentesco:</label>
                          <div class="col-sm-7">
                              <input type="text" class="form-control" id="parentesco">
                          </div>
                          <a class="btn btn-default col-sm-2" onclick="adicionarContato()">Adicionar</a>

                        </div>
                        <div class="form-group">
                            <label class="control-label col-sm-2" for="contatos">Contatos:</label>
                            <div class="col-sm-10">
                                <table class="table" style="border: 1px solid #0000004a;border-radius: 5px;border-collapse: initial;">
                                    <thead>
                                        <tr>
                                        <th>Nome</th>
                                        <th>Telefone</th>
                                        <th>Parentesco</th>
                                      </tr>
                                    </thead>
                                    <tbody id="contatos">

                                    
                                    

                                  </tbody>
                                </table>
                                
                            </div>
                        </div>
                        
                        
                        
                    </fieldset>
                    <fieldset>
                        <legend>Escola</legend>
                        
                        <div class="form-group">
                            <label class="control-label col-sm-2" for="escola">Escola:</label>
                            <div class="col-sm-10">
                                <select class="form-control" name="escola" required>
                                  <c:forEach items="${escolas}" var="x"> 
                                  <option value="${x.getId()}">${x.getNome()}</option>
                                  </c:forEach>
                                  </select>
                            </div>
                        </div>
                        <div class="form-group">
                          <label class="control-label col-sm-2" for="periodo">Período:</label>
                          <div class="col-sm-10">
                              <input type="text" class="form-control" name="periodo" maxlength="30" required >
                          </div>
                        </div>
                          <div class="form-group">
                          <label class="control-label col-sm-2" for="turma">Turma:</label>
                          <div class="col-sm-10">
                              <input type="text" class="form-control" name="turma" maxlength="30" >
                          </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-sm-2" for="escola">Veículo:</label>
                            <div class="col-sm-10">
                                <select class="form-control" name="veiculo" required>
                                  <c:forEach items="${veiculos}" var="x"> 
                                  <option value="${x.getId()}">${x.getModelo()}</option>
                                  </c:forEach>
                                  </select>
                            </div>
                        </div>
                        <div class="form-group">
                          <label class="control-label col-sm-2" for="horario_casa_ida">Saída (Casa):</label>
                          <div class="col-sm-10">
                              <input type="time" class="form-control" name="horario_casa_ida">
                          </div>
                        </div>
                        <div class="form-group">
                          <label class="control-label col-sm-2" for="horario_escola_ida">Chegada (Escola):</label>
                          <div class="col-sm-10">
                              <input type="time" class="form-control" name="horario_escola_ida">
                          </div>
                        </div>
                        <div class="form-group">
                          <label class="control-label col-sm-2" for="horario_escola_volta">Sáida (Escola):</label>
                          <div class="col-sm-10">
                              <input type="time" class="form-control" name="horario_escola_volta">
                          </div>
                        </div>
                        <div class="form-group">
                          <label class="control-label col-sm-2" for="horario_casa_ida">Chegada (Casa):</label>
                          <div class="col-sm-10">
                              <input type="time" class="form-control" name="horario_casa_volta">
                          </div>
                        </div>
                        
                    </fieldset>
                    
                    <fieldset>
                        <legend>Contrato</legend>
                        <div class="form-group">
                          <label class="control-label col-sm-2" for="nome_responsavel">Nome Responsavel:</label>
                          <div class="col-sm-10">
                              <input type="text" class="form-control" name="nome_responsavel" maxlength="100" required>
                          </div>
                        </div>
                        <div class="form-group">
                          <label class="control-label col-sm-2" for="cpf_responsavel">CPF Responsavel:</label>
                          <div class="col-sm-10">
                              <input type="text" class="form-control cpf" name="cpf_responsavel" maxlength="100">
                          </div>
                        </div>
                        <div class="form-group">
                          <label class="control-label col-sm-2" for="mensalidade">Mensalidade(R$):</label>
                          <div class="col-sm-10">
                              <input type="text" class="form-control money" name="mensalidade" maxlength=9 required>
                          </div>
                        </div>
                        <div class="form-group">
                          <label class="control-label col-sm-2" for="vencimento">Vencimento:</label>
                          <div class="col-sm-10">
                              <input type="text" class="form-control numero" maxlength=2 name="vencimento" required>
                          </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-sm-2" for="data">Data de Inicio:</label>
                            <div class="col-sm-10">
                                <input type="date" name="data-inicio" class="form-control" required value="<fmt:formatDate value="${data}"pattern="yyyy-MM-dd" />">
                            </div>
                          </div>
                        
                    </fieldset>
                    <br><br>
                    <div class="form-group"> 
                      <div class="col-sm-offset-2 col-sm-10">
                        <button type="submit" class="btn btn-default">Salvar</button>
                        <a href="Alunos" class="btn btn-default">Cancelar</a>
                      </div>
                    </div>
                    
                </form>
            </div>
        </div>
    </body>
</html>
<script type="text/javascript">
    $(".cpf").mask("000.000.000-00", {reverse: true});
    $(".rg").mask("00.000.000-0", {reverse: true});
    $(".numero").mask("00");
    $(".cep").mask("00000-000");
    $(".telefone").mask("(00) 00000-0000");
    $(".money").maskMoney({prefix:'R$ ', allowNegative: true, thousands:'.', decimal:',', affixesStay: false});
    
    function organizaLista(){
        
        var cont = 0;
            $("tbody tr").each(function(){
                $(this).attr("id", "tr" + cont);
                $(this).find("a").attr("id", cont);                
                cont++;
            });
    }
    
    
    function adicionarContato(){
        
        var tamanho_lista = $("tbody tr").length;
        if(tamanho_lista < 3){
            var nome = $("#nome_contato").val();
            var telefone = $("#telefone_contato").val();
            var parentesco = $("#parentesco").val();
            
                
                $("tbody").append('<tr><td>'+nome+'<input type="hidden" name="nome_contato" value="'+nome+'"></td><td>'+telefone+'<input type="hidden" name="telefone_contato" value="'+telefone+'"></td><td>'+parentesco+'<input type="hidden" name="parentesco" value="'+parentesco+'"></td><td><a style="color:red" id="" onclick="removerContato(this.id)"><span class="glyphicon glyphicon-remove"></span></a></td></tr>');
                organizaLista();
            
        }
        else {
            alert("Permitido apenas 3 contatos.");
        }
    }
    
    function removerContato(id){
        
        $("#tr" + id).remove();
        
        organizaLista();
    }
</script>