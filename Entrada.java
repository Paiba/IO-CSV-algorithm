package sistema.professor;

import java.io.*;
import java.text.*;
import java.util.*;
//Esta classe tem como objetivo ler e armazenar as informações dadas nos arquivos de entrada.
//Ela age como um banco de dados e é serializavel.

public class Entrada implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//**ATRIBUTOS**//
	//"Banco de dados"//
	protected LinkedList<Curso> listcursos = new LinkedList<Curso>();
	protected LinkedList<Disciplina> listdisc = new LinkedList <Disciplina>();
	protected LinkedList<Aluno> listalunos = new LinkedList<Aluno>();
	
	public Entrada(){}
	

	//**MÉTODOS Uteis**//
	public void alunoRepetidoAval()
	{
		
		for(Aluno a: listalunos)
		{
			for (Disciplina d: a.getMatriculadas())
			{
				for(Avaliacao p : d.getAvaliacoes())
				{
					int repetido=0;
					for(Nota n : p.getNotas())
					{
						if(a.equals(n.getAluno()))
						{
							repetido++;
						}
					}
					if (repetido>1)
					{
						throw new CodigoRepetidoException("O aluno "+a.getMatricula()+" foi registrado em mais de um grupo para a avaliação "+p.getCodigo()+".");
					}
				}
			}
		}
	}
	
	public boolean alunoExiste(String matricula)
	{
		for(Aluno a: listalunos)
		{
			if (a.getMatricula().equals(matricula))
			{
				return true;
			}
		}
		return false;
	}
	public boolean disciplinaExiste(String codigo)
	{
		for (Disciplina d : listdisc)
		{
			if (d.getCodigo().equals(codigo))
			{
				return true;
					
			}
		}
		return false;
	}
	public boolean cursoExiste(String codigo)
	{
		for (Curso c: listcursos)
		{
			if (c.getCodigo().equals(codigo))
			{
				return true;	
			}
		}
		return false;
	}
	public boolean avaliacaoExiste(String codigo)
	{
		for (Disciplina d: listdisc)
		{
			for(Avaliacao a : d.getAvaliacoes())
			{
				if(a.getCodigo().equals(codigo))
				{
					return true;
				}
			}
		}
		return false;
	}
	public void erroAvaliacao(LinkedList<Avaliacao> aval)
	{
		for(int i = 0; i< aval.size(); i++)
		{
			for (int j=i+1; j< aval.size(); j++)
			{
				if(aval.get(i).getCodigo().equals(aval.get(j).getCodigo()))
				{
					throw new CodigoRepetidoException("Código repetido para "+aval.get(i).getNome()+": "+aval.get(i).getCodigo()+".");
				}
			}
		}
	}
	public Curso procuraCurso(String codigo)
	{
		for(Curso c: listcursos)					//Percorre no banco de dados os cursos registrados
		{
			if(c.getCodigo().equals(codigo))				//Se o código bate com algum curso do banco de dados:
			{
				return c;							//Retorna o curso 
			}
		}
		return null;
	}
	
	
	public Aluno procurAluno(String matricula)
	{
		for (Aluno a : listalunos)
		{
			if(a.getMatricula().equals(matricula))
			{
				return a;
			}
		}
		return null;
	}
	
	public Disciplina procuraDisc(String codigo)
	{
		for (Disciplina d: listdisc)
		{
			if(d.getCodigo().equals(codigo))
			{
				return d;
			}
		}
		return null;
	}
	
	public Avaliacao procurAval(String codigo)
	{
		for (Disciplina d : listdisc)
		{
			for (Avaliacao p: d.getAvaliacoes())
			{
				if(p.getCodigo().equals(codigo))
				{
					return p;
				}
			}
		}
		return null;
	}
	
	public LinkedList<Disciplina> listaDisciplinas(String[] materias)
	{
		LinkedList<Disciplina> matriculadas = new LinkedList<Disciplina>();				//Cria uma lista vazia de disciplinas
		for (String cell: materias)														//Percorre o vetor de codigos de disciplinas
		{
			for (Disciplina d : listdisc)												//A cada codigo de disciplina percorre a lista de disciplinas do banco de dados
			{
				if (d.getCodigo().equals(cell))											//Se algum código bate:
				{
					matriculadas.add(d);                                				//Adiciona a lista de disciplinas matriculadas
				}
			}
		}
		return matriculadas;															//Retorna uma lista de disciplinas
	}
	
	//**MÉTODOS DE LEITURA DE ARQUIVOS E ARMAZENAMENTO DE INFORMAÇÕES**//
	
	
	
	//alunos.csv//
	
	
	public void leAlunos()
	{
		try
		{
			BufferedReader buffer = new BufferedReader(new FileReader("alunos.csv"));	//Cria um buffer que vai ler linha a linha o arquivo ".csv"
			String linha;																//String que irá armazenar uma linha
			String[] Coluna;															//Vetor que separará a linha em atributos
			buffer.readLine();															
			
			while((linha = buffer.readLine())!= null)									
			{
				LinkedList<Disciplina> matriculadas = new LinkedList<Disciplina>();     
				Curso curso = new Curso();
				
				Coluna = linha.split(";");
				for (int i = 0; i < Coluna.length; i++)
				    {Coluna[i] = Coluna[i].trim();}
				
				String materias[];														
				materias = Coluna[2].split(",");
				for (int i = 0; i < materias.length; i++)
				    {materias[i] = materias[i].trim();}
				
				for(String cell : materias)
				{
					if(!(disciplinaExiste(cell.trim())))
					{
						throw new CodigoNaoDefinidoException("Código de disciplina não definido usado no "+Coluna[1]+" "+Coluna[0]+": "+cell+".");
					}
				}
				if (Coluna[3].equals("G"))												
				{

					if(cursoExiste(Coluna[4]))
					{
						curso = procuraCurso(Coluna[4]);					
						matriculadas = listaDisciplinas(materias);						
						Graduando aluno = new Graduando (Coluna[0], Coluna[1], matriculadas,"G", curso);
						listalunos.add(aluno);												
					}
					else
					{
						throw new CodigoNaoDefinidoException("Código de curso não definido usado por aluno "+Coluna[0]+": "+Coluna[4]+".");
					}
				}
				else if(Coluna[3].equals("P"))											//Se o aluno é de pós
				{
					if(Coluna[4].equals("D") || Coluna[4].equals("M"))
					{
						matriculadas = listaDisciplinas(materias);							//Procura no banco de dados pelas materias em que ele está matriculado
						PosGraduando aluno = new PosGraduando (Coluna[0], Coluna[1], matriculadas,"P", Coluna[4]); //Cria aluno com as informações da linha		
						listalunos.add(aluno);												//Adiciona o aluno de pós ao bando de dados
					}
					else
					{
						throw new TipoDesconhecidoException("Tipo de aluno de pós-graduação desconhecido para "+Coluna[1]+" "+Coluna[0]+": "+Coluna[4]+".");
					}
				}
				else
				{
					throw new TipoDesconhecidoException("Tipo de aluno desconhecido para "+Coluna[1]+" "+Coluna[0]+": "+Coluna[3]+".");
				}	
			}
			for(int i = 0; i< listalunos.size(); i++)
			{
				for (int j=i+1; j< listalunos.size(); j++)
				{
					if(listalunos.get(i).getMatricula().equals(listalunos.get(j).getMatricula()))
					{
						throw new CodigoRepetidoException("Matrícula repetida para aluno: "+listalunos.get(i).getMatricula()+".");
					}
				}
			}
			Collections.sort(this.listalunos);
			buffer.close();
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
	}
	
	
	//avaliacao.csv//
	
	
	public void leAvaliacao()
	{
		try 
		{
			BufferedReader buffer = new BufferedReader(new FileReader("avaliacoes.csv"));	//Cria um buffer que vai ler linha a linha o arquivo ".csv"
			String linha;																//String que irá armazenar uma linha
			String[] Coluna;															//Vetor que separará a linha em atributos
			
			buffer.readLine();															//Pula o cabeçalho
			
			while((linha = buffer.readLine())!= null)									//Le linha a linha
			{
				Coluna = linha.split(";");											//Separa a string linha a cada ";" e armazena cada elemento no vetor tableline
				for (int i = 0; i < Coluna.length; i++)
				    Coluna[i] = Coluna[i].trim();
				
				if(!disciplinaExiste(Coluna[0]))
				{
					throw new CodigoNaoDefinidoException("Código de disciplina não definido usado no "+Coluna[2]+", "+Coluna[1]+": "+Coluna[0]+".");
				}
				if (Coluna[4].equals("P"))											//Se o tipo da avaliacao e prova:
				{
					if(Coluna.length > 6)
					{
						throw new TamanhoInvalidoException("Tamanho máximo de grupo especificado para a prova "+Coluna[1]+": "+Coluna[6]+".");
					}
					for (int i=0; i<listdisc.size() ; i++)								//for que percorre a lista de disciplinas afim de checar os matchings entre avaliações e disciplinas via codigo
					{
						if(listdisc.get(i).getCodigo().equals(Coluna[0]))			//Se a disciplina com este codigo esta no banco de dados
						{
							int peso = Integer.parseInt(Coluna[3]);
							if(peso<=0)
							{
								throw new PesoInvalidoException("Peso de avaliação inválido para "+Coluna[1]+": "+peso+".");
							}
							SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
							String dateInString = Coluna[5];
							Date date = formatter.parse(dateInString);
							Prova a1 = new Prova(Coluna[1], Coluna[2], peso, date); 	// Cria uma prova com as informações da linha
							a1.setTipo('P');
							listdisc.get(i).adicionaAvaliacao(a1);									//Coloca a prova na lista de avaliações da disciplina
						}
					}
				}
				else if (Coluna[4].equals("T"))											//Se o tipo da avaliacao é trabalho:
				{
					for (Disciplina d :listdisc)									//for que percorre a lista de disciplinas afim de checar os matchings entre avaliações e disciplinas via codigo
					{
						if(d.getCodigo().equals(Coluna[0]))				//Se a disciplina com este codigo esta no banco de dados
						{
							int peso = Integer.parseInt(Coluna[3]);
							if(peso<=0)
							{
								throw new PesoInvalidoException("Peso de avaliação inválido para "+Coluna[1]+": "+peso+".");
							}
							int tam = Integer.parseInt(Coluna[6]);
							if(tam<=0)
							{
								throw new TamanhoInvalidoException("Tamanho máximo de grupo inválido para trabalho "+Coluna[1]+": "+tam+".");
							}
							SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
							String dataNaString = Coluna[5];
							Date date = formatter.parse(dataNaString);
							Trabalho a1 = new Trabalho(Coluna[1], Coluna[2], peso, date, tam); 	// Cria trabalho com as informações da linha
							a1.setTipo('T');
							d.adicionaAvaliacao(a1);										   	//Coloca o trabalho na lista de avaliações da disciplina
						}
					}
				}
				else if(Coluna[4].equals("F"))
				{
					if(Coluna.length > 6)
					{
						throw new TamanhoInvalidoException("Tamanho máximo de grupo especificado para a prova "+Coluna[1]+": "+Coluna[6]+".");
					}
					for(Disciplina d: listdisc)
					{
						if(d.getCodigo().equals(Coluna[0]))
						{
							int peso = Integer.parseInt(Coluna[3]);
							if(peso<=0)
							{
								throw new PesoInvalidoException("Peso de avaliação inválido para "+Coluna[1]+": "+peso+".");
							}
							SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
							String dataNaString = Coluna[5];
							Date date = formatter.parse(dataNaString);
							Prova a1 = new Prova(Coluna[1], Coluna[2], peso, date);
							a1.setTipo('F');
							d.adicionaAvaliacao(a1);
						}
					}
				}
				else
				{
					throw new TipoDesconhecidoException("Tipo de avaliação desconhecida para "+Coluna[0]+": "+Coluna[4]+".");
				}
			}
			for(Disciplina d: listdisc)
			{
				erroAvaliacao(d.getAvaliacoes());
			}
			for(int i=0; i<listdisc.size();i++)
			{
				if(listdisc.get(i).getAvaliacoes().size()==0)
				{
					throw new AvaliacoesVaziaException("A disciplina "+listdisc.get(i).getCodigo()+" não possui nenhuma avaliação cadastrada.");
				}
				Collections.sort(listdisc.get(i).getAvaliacoes());
			}
			buffer.close();
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
		} catch (ParseException e) {
			System.out.println("Erro de formatação");
			e.printStackTrace();
		}
	}
	
	
	//curso.csv//
	
	public void leCursos(){}
	{
		try 
		{
			BufferedReader buffer = new BufferedReader(new FileReader("cursos.csv"));	//Cria um buffer que vai ler linha a linha o arquivo ".csv"
			String linha;																//String que irá armazenar uma linha
			String[] Coluna;															//Vetor que separará a linha em atributos
			
			buffer.readLine();															//Pula o cabeçalho
			
			while((linha = buffer.readLine())!= null)									//Le linha a linha
			{
				Coluna = linha.split(";");												//Separa a string linha a cada ";" e armazena cada elemento no vetor tableline
				for (int i = 0; i < Coluna.length; i++)
				    Coluna[i] = Coluna[i].trim();
				
				Curso c1 = new Curso(Coluna[0], Coluna[1]);								//Cria um curso novo utilizando as informações da linha
				listcursos.add(c1);														//Adiciona no "banco de dados" da classe entrada
			}
			for(int i=0; i < listcursos.size();i++)
			{
				for (int j =i+1; j<listcursos.size();j++)
				{
					if(listcursos.get(i).getCodigo()==listcursos.get(j).getCodigo())
					{
						throw new CodigoRepetidoException("Código repetido para "+listcursos.get(i).getNome()+": "+listcursos.get(i).getCodigo()+".");
					}
				}
			}
			Collections.sort(listcursos);
			buffer.close();
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
	}
	
	//disciplinas.csv//
	
	public void leDisciplina() 
	{
		try 
		{
			BufferedReader buffer = new BufferedReader(new FileReader("disciplinas.csv"));	//Cria um buffer que vai ler linha a linha o arquivo ".csv"
			String linha;																//String que irá armazenar uma linha
			String[] Coluna;															//Vetor que separará a linha em atributos
			
			buffer.readLine();															//Pula o cabeçalho
			
			while((linha = buffer.readLine())!= null)									//Le linha a linha
			{
				
				Coluna = linha.split(";");											//Separa a string linha a cada ";" e armazena cada elemento no vetor tableline
				for (int i = 0; i < Coluna.length; i++)
				    Coluna[i] = Coluna[i].trim();
				
				Disciplina d1 = new Disciplina(Coluna[0], Coluna[1]);				//Cria disciplina a partir das informações da linha
				listdisc.add(d1);
			}
			for(int i=0; i < listdisc.size();i++)
			{
				for (int j =i+1; j<listdisc.size();j++)
				{
					if(listdisc.get(i).getCodigo().equals(listdisc.get(j).getCodigo()))
					{
						throw new CodigoRepetidoException("Código repetido para "+listdisc.get(i).getNome()+": "+listdisc.get(i).getCodigo()+".");
					}
				}
			}
			Collections.sort(listdisc);
			buffer.close();
		} 
		catch (FileNotFoundException e) 
		{
			System.out.println("Erro de I/O");
			e.printStackTrace();
		} catch (IOException e) 
		{
			System.out.println("Erro de I/O");
			e.printStackTrace();
		}
	}
	
	
	//notas.csv//
	
	public void leNotas() 
	{
		try
		{
			BufferedReader buffer = new BufferedReader(new FileReader("notas.csv"));	
			String linha;																
			String[] Coluna;															
			buffer.readLine();															

			while((linha = buffer.readLine())!= null)									
			{
				Coluna = linha.split(";");
				for (int i = 0; i < Coluna.length; i++)
				    Coluna[i] = Coluna[i].trim();
				
				String[] alunos = Coluna[1].split(",");
				for (int i = 0; i < alunos.length; i++)
				    alunos[i] = alunos[i].trim();
				
				if(alunos.length==1)
				{
					for(Disciplina d: listdisc)
					{
						if(d.getAvaliacoes().contains(procurAval(Coluna[0])))
						{
							if(!procurAluno(Coluna[1]).getMatriculadas().contains(d))
							{
								throw new AlunoNaoMatriculadoException("O aluno "+Coluna[1]+" possui nota na avaliação "+Coluna[0]+" da disciplina "+d.getCodigo()+", porém não encontra-se matriculado nesta disciplina.");
							}
						}
					}
					if (procurAval(Coluna[0]).getTipo()=='P'|| procurAval(Coluna[0]).getTipo()=='F')
					{
						NumberFormat formato = NumberFormat.getInstance(Locale.FRANCE);
						Number decimal = formato.parse(Coluna[2]);
						double nota = decimal.doubleValue();
						if (!alunoExiste(Coluna[1]))
						{
							throw new CodigoNaoDefinidoException("Matrícula de aluno não definida usada na planilha de notas associada à avaliação "+Coluna[0]+": "+Coluna[1]+".");
						}
						if(!(nota<=10 && nota>=0))
						{
							throw new NotaInvalidaException("Nota inválida para a avaliação "+Coluna[0]+" do(s) aluno(s) "+Coluna[1]+": "+nota+".");
						}
						if(avaliacaoExiste(Coluna[0]))
						{
							Aluno a1 = procurAluno(Coluna[1]);
							Nota n1 = new Nota(nota, a1);
							procurAval(Coluna[0]).getNotas().add(n1);	
						}
						else
						{
							throw new CodigoNaoDefinidoException("Código de avaliação não definido usado na planilha de notas, associado(s) ao(s) aluno(s) "+Coluna[1]+": "+Coluna[0]+".");
						}
					}
					else if (procurAval(Coluna[0]).getTipo()=='T')
					{
						for(Disciplina d: listdisc)
						{
							if(d.getAvaliacoes().contains(procurAval(Coluna[0])))
							{
								if(!procurAluno(Coluna[1]).getMatriculadas().contains(d))
								{
									throw new AlunoNaoMatriculadoException("O aluno "+Coluna[1]+" possui nota na avaliação "+Coluna[0]+" da disciplina "+d.getCodigo()+", porém não encontra-se matriculado nesta disciplina.");
								}
							}
						}
						NumberFormat formato = NumberFormat.getInstance(Locale.FRANCE);
						Number decimal = formato.parse(Coluna[2]);
						double nota = decimal.doubleValue();
						if(!(nota<=10 && nota>=0))
						{
							String erro;
							erro ="Nota inválida para avaliação "+Coluna[0]+" do(s) aluno(s) ";
							for(int i=0; i<alunos.length; i++)
							{
								erro = erro+alunos[i];
								if(i<alunos.length-1)
								{
									erro= erro+", ";
								}
							}
							erro= erro+": "+String.format("%.1f", nota)+".";
							throw new NotaInvalidaException(erro.trim());
						}
						((Trabalho)procurAval(Coluna[0])).setSomanota((((Trabalho)procurAval(Coluna[0])).getSomanota())+nota);
						((Trabalho)procurAval(Coluna[0])).setQtdgrupos((((Trabalho)procurAval(Coluna[0])).getQtdgrupos())+1);
						if (!alunoExiste(Coluna[1]))
						{
							throw new CodigoNaoDefinidoException("Matrícula de aluno não definida usada na planilha de notas associada à avaliação "+Coluna[0]+": "+Coluna[1]+".");
						}
						if(!(nota<=10 && nota>=0))
						{
							throw new NotaInvalidaException("Nota inválida para a avaliação "+Coluna[0]+" do(s) aluno(s) "+Coluna[1]+": "+nota+".");
						}
						if(avaliacaoExiste(Coluna[0]))
						{
							Aluno a1 = procurAluno(Coluna[1]);
							Nota n1 = new Nota(nota, a1);
							procurAval(Coluna[0]).getNotas().add(n1);	
						}
						else
						{
							throw new CodigoNaoDefinidoException("Código de avaliação não definido usado na planilha de notas, associado(s) ao(s) aluno(s) "+Coluna[1]+": "+Coluna[0]+".");
						}
					}
													
				}
				else
				{
					NumberFormat formato = NumberFormat.getInstance(Locale.FRANCE);
					Number decimal = formato.parse(Coluna[2]);
					double nota = decimal.doubleValue();
					if(!(nota<=10 && nota>=0))
					{
						String erro;
						erro = "Nota inválida para avaliação "+Coluna[0]+" do(s) aluno(s) ";
						for(int i=0; i<alunos.length; i++)
						{
							erro = erro+alunos[i];
							if(i<alunos.length-1)
							{
								erro= erro+", ";
							}
						}
						erro= erro+": "+String.format("%.1f", nota)+".";
						throw new NotaInvalidaException(erro.trim());
					}
					((Trabalho)procurAval(Coluna[0])).setSomanota((((Trabalho)procurAval(Coluna[0])).getSomanota())+nota);
					((Trabalho)procurAval(Coluna[0])).setQtdgrupos((((Trabalho)procurAval(Coluna[0])).getQtdgrupos())+1);
					
					for (int i=0; i<alunos.length ; i++)
					{
						for(Disciplina d: listdisc)
						{
							if(d.getAvaliacoes().contains(procurAval(Coluna[0])) && (!procurAluno(alunos[i]).getMatriculadas().contains(d)))
							{
								throw new AlunoNaoMatriculadoException("O aluno "+alunos[i]+" possui nota na avaliação "+Coluna[0]+" da disciplina "+d.getCodigo()+", porém não encontra-se matriculado nesta disciplina.");
							}
						}
						
						if (!alunoExiste(alunos[i]))
						{
							throw new CodigoNaoDefinidoException("Matricula de aluno não definida usada na planilha de notas associada à avaliação "+Coluna[0]+": "+alunos[i]+".");
						}
					
						else
						{
							if(avaliacaoExiste(Coluna[0]))
							{
								Aluno a1 = procurAluno(alunos[i]);
								Nota n1 = new Nota(nota, a1);
								procurAval(Coluna[0]).getNotas().add(n1);
								
							}
							else
							{
								String erro;
								erro ="Código de avaliação não definido usado na planilha de notas, associado(s) ao(s) aluno(s) ";
								for(int j=0; j<alunos.length; j++)
								{
									erro = erro+alunos[j];
									if(j<alunos.length-1)
									{
										erro= erro+", ";
									}
								}
								erro= erro+": "+Coluna[0]+".";
								throw new NotaInvalidaException(erro.trim());
							}
						}
					}
				}
			
			}
			this.alunoRepetidoAval();
			buffer.close();
			
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
		} catch (ParseException e)
		{
			System.out.println("Erro de formatação");
			e.printStackTrace();
		}
	}
	
	
	public void Imprime()
	{
		for (Aluno a: listalunos)
		{
			System.out.println(a.getNome());
		}
	}
	

}
