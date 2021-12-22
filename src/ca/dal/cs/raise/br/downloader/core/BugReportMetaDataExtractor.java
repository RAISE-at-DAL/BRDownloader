package ca.dal.cs.raise.br.downloader.core;

import java.net.URL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BugReportMetaDataExtractor {

	int bugID;
	public static boolean isMozilla;

	public BugReportMetaDataExtractor(int bugID) {
		this.bugID = bugID;
	}

	public void setBugRepository(boolean isMozila) {
		this.isMozilla = isMozila;
	}

	public Document extractXMlDocument() {
		Document document = null;
		try {
			String xmlURL = prepareXMLURL(bugID);
			document = Jsoup.parse(new URL(xmlURL), 5000);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return document;
	}

	protected String prepareXMLURL(int bugID) {
		String xmlURL = new String();
		if (isMozilla) {
			xmlURL = "https://bugzilla.mozilla.org/show_bug.cgi?ctype=xml&id=" + bugID;
		} else {
			xmlURL = "https://bugs.eclipse.org/bugs/show_bug.cgi?ctype=xml&id=" + bugID;
		}
		return xmlURL;
	}

	public BRMetaData extractMetaData(Document document) {
		BRMetaData brMetadata = new BRMetaData();

		brMetadata.bugID = this.bugID;

		Element classification = document.select("classification").first();
		brMetadata.classification = classification.text();
		Element product = document.select("product").first();
		brMetadata.product = product.text();
		Element component = document.select("component").first();
		brMetadata.component = component.text();
		Element priority = document.select("priority").first();
		brMetadata.priority = priority.text();
		Element severity = document.select("bug_severity").first();
		brMetadata.severity = severity.text();

		Elements dependedBugs = document.select("dependson");
		if (!dependedBugs.isEmpty()) {
			brMetadata.dependsOn = true;
			brMetadata.numDependsOn = dependedBugs.size();
			for (Element elem : dependedBugs) {
				int depended = Integer.parseInt(elem.text().trim());
				brMetadata.depends.add(depended);
			}
		}
		Elements blockedBugs = document.select("blocked");
		if (!blockedBugs.isEmpty()) {
			brMetadata.blocks = true;
			brMetadata.numBlocked = blockedBugs.size();
			for (Element elem : blockedBugs) {
				int blocked = Integer.parseInt(elem.text());
				brMetadata.blocked.add(blocked);
			}
		}

		Element duplicate = document.getElementsByTag("dup_id").first();
		if (duplicate != null) {
			int duplicateBug = Integer.parseInt(duplicate.text().trim());
			brMetadata.duplicates.add(duplicateBug);
		}

		Element reporter = document.select("reporter").first();
		Element assignee = document.select("assigned_to").first();

		if (reporter.text().equals(assignee.text())) {
			brMetadata.reporterIsAssignee = true;
		}

		Elements ccs = document.select("cc");
		if (!ccs.isEmpty()) {
			brMetadata.hasCC = true;
			brMetadata.numCC = ccs.size();
		}

		Elements comments = document.select("long_desc");
		if (!comments.isEmpty()) {
			brMetadata.numberOfComments = comments.size();
		}

		Element vote = document.select("votes").first();
		try {
			brMetadata.numberOfVotes = Integer.parseInt(vote.text().trim());
		} catch (Exception exc) {
		}

		Elements attachments = document.select("attachment > attachid");
		if (!attachments.isEmpty()) {
			brMetadata.hasAttachment = true;
			brMetadata.numAttachment = attachments.size();
		}

		// adding the textual information.
		Element shortDesc = document.select("short_desc").first();
		brMetadata.title = shortDesc.text();

		// adding the description
		brMetadata.description = comments.first().select("thetext").first().text();

		return brMetadata;
	}

	public static void main(String[] args) {

		int bugID = 1754; // has a duplicate

		// int bugID = 536330; //no duplicate

		BugReportMetaDataExtractor extractor = new BugReportMetaDataExtractor(bugID);
		extractor.setBugRepository(false);

		Document document = extractor.extractXMlDocument();
		BRMetaData metadata = extractor.extractMetaData(document);

		System.out.println(metadata.classification);
		System.out.println(metadata.product);
		System.out.println(metadata.component);

		System.out.println(metadata.priority);
		System.out.println(metadata.severity);

		System.out.println(metadata.dependsOn);
		System.out.println(metadata.numDependsOn);
		System.out.println(metadata.depends);
		System.out.println(metadata.blocks);
		System.out.println(metadata.numBlocked);
		System.out.println(metadata.blocked);
		System.out.println(metadata.duplicates);
		System.out.println(metadata.reporterIsAssignee);
		System.out.println(metadata.hasCC);
		System.out.println(metadata.numCC);
		System.out.println(metadata.numberOfComments);
		System.out.println(metadata.numberOfVotes);
		System.out.println(metadata.hasAttachment);
		System.out.println(metadata.numAttachment);
		System.out.println(metadata.title);
		System.out.println(metadata.description);

	}
}
