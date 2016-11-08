package sistema.professor;

import java.io.*;


public class Main {
	
	public static void main(String[] args){
		
		
		Entrada bancodedados = new Entrada();
		
		int indice = 0;
		boolean isWriteOnly=false;
		boolean isReadOnly =false;
		boolean a =false;
		boolean c =false;
		boolean d = false;
		boolean p =false;
		boolean n = false;
		
		int i=0;
		
		while (i<args.length)//Conferindo se os argumentos estão ordenados com os comandos
		{
			if(args[i].equals("-a"))
			{
				if(!args[i+1].equals("alunos.csv"))
				{
					System.out.println("Parâmetros em ordem errada ou parâmetros errados");
					System.exit(0);
				}
			}
			if(args[i].equals("-c"))
			{
				if(!args[i+1].equals("cursos.csv"))
				{
					System.out.println("Parâmetros em ordem errada ou parâmetros errados");
					System.exit(0);
				}
			}
			if(args[i].equals("-d"))
			{
				if(!args[i+1].equals("disciplinas.csv"))
				{
					System.out.println("Parâmetros em ordem errada ou parâmetros errados");
					System.exit(0);
				}
			}
			if(args[i].equals("-p"))
			{
				if(!args[i+1].equals("avaliacoes.csv"))
				{
					System.out.println("Parâmetros em ordem errada ou parâmetros errados");
					System.exit(0);
				}
			}
			if(args[i].equals("-n"))
			{
				if(!args[i+1].equals("notas.csv"))
				{
					System.out.println("Parâmetros em ordem errada ou parâmetros errados");
					System.exit(0);
				}
			}
			i++;
		}
		while(indice < args.length)//Confere se todos os comandos obrigatórios (a, c, d, p, n) e não obrigatórios(readonly, writeonly) existem entre os parametros
		{
			if(args[indice].equals("--read-only"))
			{
				isReadOnly=true;
			}
			if(args[indice].equals("--write-only"))
			{
				isWriteOnly=true;
			}
			if(args[indice].equals("-a"))
			{
				a=true;
			}
			if(args[indice].equals("-c"))
			{
				c=true;
			}
			if(args[indice].equals("-d"))
			{
				d=true;
			}
			if(args[indice].equals("-p"))
			{
				p=true;
			}
			if(args[indice].equals("-n"))
			{
				n=true;
			}
			indice++;
		}
		if(!(a && c && d && p && n))//Se não está recebendo todos os parametros obrigatórios referentes a arquivos
		{
			System.out.println("Parâmetros em ordem errada ou parâmetros errados");
			System.exit(0);
		}
		
		try
		{
			bancodedados.leCursos();
			bancodedados.leDisciplina();
			bancodedados.leAlunos();
			bancodedados.leAvaliacao();
			bancodedados.leNotas();
		}
		catch(AlunoNaoMatriculadoException e)
		{
			System.out.println(e.getMessage());
		}
		catch(AvaliacoesVaziaException e)
		{
			System.out.println(e.getMessage());
		}
		catch(CodigoNaoDefinidoException e)
		{
			System.out.println(e.getMessage());
		}
		catch(CodigoRepetidoException e)
		{
			System.out.println(e.getMessage());
		}
		catch(NotaInvalidaException e)
		{
			System.out.println(e.getMessage());
		}
		catch(PesoInvalidoException e)
		{
			System.out.println(e.getMessage());
		}
		catch(TamanhoInvalidoException e)
		{
			System.out.println(e.getMessage());
		}
		catch(TipoDesconhecidoException e)
		{
			System.out.println(e.getMessage());
		}
		try
		{
			if(isReadOnly && isWriteOnly || !(isReadOnly) && !(isWriteOnly))
			{
				Saida saida = new Saida();
				saida.geraPauta(bancodedados);
				saida.geraEstatisticaDisc(bancodedados);
				saida.geraEstatisticaAval(bancodedados);
				
				
			}
			else
			{
				if(isReadOnly)
				{
					FileOutputStream novo = new FileOutputStream ("semestre.dat");
					ObjectOutputStream cria = new ObjectOutputStream(novo);
					cria.writeObject(bancodedados);
					cria.close();
					novo.close();
				}
				else if (isWriteOnly)
				{
					FileInputStream le = new FileInputStream("semestre.dat");
					ObjectInputStream arq = new ObjectInputStream(le);
					Entrada bancodedados1 = (Entrada) arq.readObject();
					
					le.close();
					arq.close();
					
					Saida saida1 = new Saida();
					saida1.geraPauta(bancodedados1);
					saida1.geraEstatisticaDisc(bancodedados1);
					saida1.geraEstatisticaAval(bancodedados1);
					
					
					
				}
			}
		}
		catch (FileNotFoundException e) 
		{
			System.out.println("Erro de I/O");
			e.printStackTrace();
		} 
		catch (IOException e) 
		{	
			System.out.println("Erro de I/O");
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) 
		{
			e.printStackTrace();
		}
	}

}
	

