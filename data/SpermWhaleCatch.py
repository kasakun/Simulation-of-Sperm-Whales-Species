# Created on 03/30/2018
# Taoyouwei Gao

import urllib.request
import time
import csv
from bs4 import BeautifulSoup


def getYearUrl(year):

	url = 'http://luna.pos.to/whale/sta_'+year+'.html'
	soup=BeautifulSoup(urllib.request.urlopen(url),"lxml")
	#temp=urllib.request.urlopen(req).read()
	trs = soup.find_all('tr')
	uilist = []
	for tr in trs:
		ui = []
		for td in tr:
			ui.append(td.string)
		uilist.append(ui)
	index=0
	for x in range(0,len(uilist)):
		if uilist[x][1]=='TOTAL':
			index=x
			break
	print('Downloading '+year+'.csv')
	with open("/Users/matthewgao/Downloads/python/spermWhaleCatch.csv","a+") as csvfile:
	    writer = csv.writer(csvfile)
	    list=[]
	    list.append(year)
	    list.append(uilist[index][11])
	    writer.writerow(list)
	    csvfile.close()
if __name__ == '__main__':

	for i in range(1910,2014):
		getYearUrl(str(i))
