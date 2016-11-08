package sistema.professor;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;

public class Saida implements Serializable{
	
	public void geraPauta(Entrada entrada){
		try
		{
			for (Disciplina d : entrada.listdisc) //ce não pode acessar list disc dai, tem que fazer uma função get dentro de entrada
			{
				FileOutputStream arquivo = new FileOutputStream("1-pauta-"+d.getCodigo()+".csv");
				PrintWriter pw = new PrintWriter(arquivo);
				pw.print("Matrícula;Aluno;");
				
				for (Avaliacao p : d.getAvaliacoes()) 
				{
						if(!(p.getTipo()=='F'))
							pw.print(p.getCodigo()+";");
				}
				pw.println("Média Parcial;Prova Final;Média Final");
				for (Aluno a : entrada.listalunos)
				{
					if(a.getMatriculadas().contains(d))
					{
						pw.print(a.getMatricula()+";"+a.getNome()+";");
						for(Avaliacao p: d.getAvaliacoes())
						{
							if (!(p.getTipo()=='F'))
							{
								for(Nota n : p.getNotas() )
								{
									if(n.getAluno()==a)
									{
										pw.print (String.format("%.2f", n.getNota())+";");
									}
								}
							}
						}
						if(a.calculaMediaParcial(d)<7)
						{
							pw.print(String.format("%.2f", a.calculaMediaParcial(d)));
							pw.print(";"+String.format("%.2f", a.provaFinal(d))+";");
							pw.print(String.format("%.2f", a.calculaMediaFinal(d)));
						}
						else
						{
							pw.print(String.format("%.2f", a.calculaMediaParcial(d)));
							pw.print(";-;");
							pw.print(String.format("%.2f", a.calculaMediaFinal(d)));
						}
						pw.println("");
					}
					
				}
				
				pw.close();
			}
			
			
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public void geraEstatisticaDisc(Entrada entrada)
	{
		try
		{
			LinkedList<OrdenaRelatorio2> ordem = new LinkedList<OrdenaRelatorio2>();
			FileOutputStream disciplinas = new FileOutputStream("2-disciplinas.csv");
			PrintWriter pw = new PrintWriter(disciplinas);
			pw.println("Código;Disciplina;Curso;Média;% Aprovados");
			for(Disciplina d: entrada.listdisc)
			{
				double mediaSala = 0;
				double tamSala = 0;
				double aprovados = 0;
				for(Curso c: entrada.listcursos)
				{
					for(Aluno a: entrada.listalunos)
					{
						if (a.getTipo()=='G' && ((Graduando)a).getCurso().getCodigo().equals(c.getCodigo()) && a.getMatriculadas().contains(d))
						{
							
							mediaSala = mediaSala+ a.calculaMediaFinal(d);
							tamSala++;
							if (a.calculaMediaFinal(d)>=5)
							{
								aprovados++;
							}
						}
					}
					if(!(tamSala==0))
						ordem.add(new OrdenaRelatorio2((mediaSala/tamSala), d.getCodigo(),c.getNome(), (d.getCodigo()+";"+d.getNome()+";"+c.getNome()+";"+String.format("%.2f", (mediaSala/tamSala))+";"+String.format("%.1f", 100*(aprovados/tamSala))+"%")));
					aprovados=0;
					tamSala=0;
					mediaSala=0;
				}
				for(Aluno a: entrada.listalunos)
				{
					if (a.getTipo()=='P' && ((PosGraduando)a).getGrau()=='M' && a.getMatriculadas().contains(d))
					{
						mediaSala = mediaSala+ a.calculaMediaFinal(d);
						tamSala++;
						if (a.calculaMediaFinal(d)>=5)
						{
							aprovados++;
						}
					}
				}
				if(!(tamSala==0))
					ordem.add(new OrdenaRelatorio2((mediaSala/tamSala), d.getCodigo(), "Mestrado",(d.getCodigo()+";"+d.getNome()+";Mestrado;"+String.format("%.2f", (mediaSala/tamSala))+";"+String.format("%.1f", 100*(aprovados/tamSala))+"%")));
				aprovados=0;
				tamSala=0;
				mediaSala=0;
				for(Aluno a: entrada.listalunos)
				{
					if (a.getTipo()=='P' && ((PosGraduando)a).getGrau()=='D' && a.getMatriculadas().contains(d))
					{
						mediaSala = mediaSala+ a.calculaMediaFinal(d);
						tamSala++;
						if (a.calculaMediaFinal(d)>=5)
						{
							aprovados++;
						}
					}
				}
				if(!(tamSala==0))
					ordem.add(new OrdenaRelatorio2((mediaSala/tamSala), d.getCodigo(),"Doutorado", (d.getCodigo()+";"+d.getNome()+";Doutorado;"+String.format("%.2f", (mediaSala/tamSala))+";"+String.format("%.1f", 100*(aprovados/tamSala))+"%")));
			}
			Collections.sort(ordem);
			for(OrdenaRelatorio2 o: ordem)
			{
				pw.println(o.getLinha());
			}
			pw.close();
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void geraEstatisticaAval(Entrada entrada)
	{
		try
		{
			FileOutputStream avaliacoes = new FileOutputStream("3-avaliacoes.csv");
			PrintWriter pw = new PrintWriter(avaliacoes);
			pw.println("Disciplina;Código;Avaliação;Data;Média");
			for (Disciplina d : entrada.listdisc) 
			{
				for (Avaliacao a : d.getAvaliacoes()) 
				{
					if(a.getTipo()!='F')
					{
						if(a.getTipo()=='P')
						{
							SimpleDateFormat forma = new SimpleDateFormat("dd/MM/yyyy");
							pw.println(d.getCodigo()+";"+a.getCodigo()+";"+a.getNome()+";"+forma.format(a.getData())+";"+String.format("%.2f",((Prova)a).calculaMediaSala()));
						}
						else if (a.getTipo()=='T')
						{
							SimpleDateFormat forma = new SimpleDateFormat("dd/MM/yyyy");
							pw.println(d.getCodigo()+";"+a.getCodigo()+";"+a.getNome()+";"+forma.format(a.getData())+";"+String.format("%.2f",((Trabalho)a).calculaMediaSala()));
						}
					}
					
				}
			}
				
				
			pw.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
			
	}
				//---------------------------------------------------------------------------------------
}

