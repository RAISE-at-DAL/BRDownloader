package ca.dal.cs.raise.br.downloader.core;

import java.util.ArrayList;

public class BRMetaData {

	int bugID;

	String classification;
	String product;
	String component;
	String priority;
	String severity;
	public boolean dependsOn = false;
	int numDependsOn = 0;
	public ArrayList<Integer> depends = new ArrayList<>();

	public boolean blocks = false;
	int numBlocked = 0;
	public ArrayList<Integer> blocked = new ArrayList<>();

	public ArrayList<Integer> duplicates = new ArrayList<>();

	boolean reporterIsAssignee = false;
	boolean hasCC = false;
	int numCC = 0;
	int numberOfComments = 0;
	int numberOfVotes = 0;
	boolean hasAttachment = false;
	int numAttachment = 0;

	// textual properties
	public String title;
	public String description;

	// sentiment
	int sentimentLevel;

}
